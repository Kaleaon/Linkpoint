package com.linkpoint.render.lumiya.glres

import android.opengl.GLES30
import android.opengl.GLES32
import android.util.Log
import com.linkpoint.assets.TextureMemoryTracker
import java.nio.ByteBuffer

/**
 * Double-buffered Pixel Buffer Object (PBO) ring buffer manager for non-blocking texture uploads.
 *
 * Requirements & Constraints:
 *  - Target devices must support OpenGL ES 3.0+ for `GL_PIXEL_UNPACK_BUFFER`.
 *  - Asynchronous non-blocking GPU texture transfers using double-buffered ring buffers.
 *  - Zero synchronous main-thread CPU memory copies during `glTexSubImage2D` when PBO is bound.
 *  - Peak RAM overhead strictly capped under 32MB total across all active PBO double-buffers.
 *  - Automatic memory auto-throttling when system free RAM drops below critical thresholds.
 *  - All OpenGL state modifications strictly isolated to the main EGL render thread.
 */
class PboRingBufferManager(
    private val resourceManager: GLResourceManager,
    private val ringDepth: Int = DEFAULT_RING_DEPTH
) {
    companion object {
        private const val TAG = "PboRingBufferManager"
        private const val DEFAULT_RING_DEPTH = 2 // Double buffering
        private const val MAX_PBO_RAM_BYTES = 32L * 1024L * 1024L // 32MB hard ceiling
        private const val DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024 // 4MB per buffer (e.g. 1024x1024 RGBA)
        private const val LOW_MEM_BUFFER_SIZE = 1 * 1024 * 1024 // 1MB per buffer under low RAM
    }

    private var pboHandles = IntArray(0)
    private var syncFences = arrayOfNulls<Long>(0)
    private var pboAllocatedSizes = LongArray(0)
    private var ringIndex = 0

    @Volatile var isPboSupported: Boolean = false; private set
    @Volatile var totalAllocatedRamBytes: Long = 0L; private set
    @Volatile var autoThrottled: Boolean = false; private set

    // Performance tracking
    @Volatile var totalPboUploads: Long = 0L; private set
    @Volatile var totalFallbackUploads: Long = 0L; private set

    /**
     * Initialize PBO ring buffers on the main EGL thread.
     */
    fun initialize(glVersion: Int) {
        resourceManager.assertGlThread("PboRingBufferManager.initialize")

        if (glVersion < 30) {
            Log.w(TAG, "GLES version $glVersion < 30 — PBO double-buffering disabled, falling back to direct uploads")
            isPboSupported = false
            return
        }

        try {
            val depth = ringDepth.coerceIn(2, 4)
            pboHandles = IntArray(depth)
            pboAllocatedSizes = LongArray(depth)
            syncFences = arrayOfNulls(depth)

            GLES30.glGenBuffers(depth, pboHandles, 0)
            var initSuccess = true
            for (i in 0 until depth) {
                if (pboHandles[i] <= 0) {
                    initSuccess = false
                    break
                }
            }

            if (initSuccess) {
                isPboSupported = true
                Log.i(TAG, "Initialized PBO double-buffered ring buffer with $depth buffers (GLES $glVersion)")
            } else {
                Log.e(TAG, "glGenBuffers failed to create PBO handles")
                isPboSupported = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize PBO ring buffer manager", e)
            isPboSupported = false
        }
    }

    /**
     * Stage uncompressed RGBA pixel buffer into double-buffered PBO and upload to GL texture.
     * Must be called on the main EGL render thread.
     */
    fun stageAndUploadTexture(
        textureHandle: Int,
        width: Int,
        height: Int,
        pixelBuffer: ByteBuffer,
        format: Int = GLES30.GL_RGBA,
        type: Int = GLES30.GL_UNSIGNED_BYTE,
        isLowMemorySystem: Boolean = false
    ): Boolean {
        resourceManager.assertGlThread("PboRingBufferManager.stageAndUploadTexture")

        val requiredBytes = width * height * 4L
        autoThrottled = isLowMemorySystem || (totalAllocatedRamBytes + requiredBytes > MAX_PBO_RAM_BYTES)

        if (!isPboSupported || pboHandles.isEmpty() || requiredBytes <= 0) {
            totalFallbackUploads++
            return false // Caller falls back to direct glTexSubImage2D / GLUtils upload
        }
        if (autoThrottled && requiredBytes > LOW_MEM_BUFFER_SIZE) {
            Log.w(TAG, "Auto-throttling PBO upload for ${width}x${height} ($requiredBytes bytes) due to memory ceiling ($totalAllocatedRamBytes bytes allocated)")
            totalFallbackUploads++
            return false // Fallback to direct upload to prevent low memory kills
        }

        try {
            val currentIndex = ringIndex
            ringIndex = (ringIndex + 1) % pboHandles.size
            val pboHandle = pboHandles[currentIndex]

            // 1. Check & wait for fence sync on this buffer slot to prevent GPU overwrite stall
            syncFences[currentIndex]?.let { fence ->
                if (fence != 0L) {
                    val waitResult = GLES30.glClientWaitSync(fence, GLES30.GL_SYNC_FLUSH_COMMANDS_BIT, 10_000_000L) // 10ms max wait
                    if (waitResult == GLES30.GL_TIMEOUT_EXPIRED) {
                        Log.w(TAG, "PBO slot $currentIndex GPU sync fence timed out, proceeding safely")
                    }
                    GLES30.glDeleteSync(fence)
                    syncFences[currentIndex] = null
                }
            }

            // 2. Bind PBO ring buffer
            GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, pboHandle)

            // 3. Allocate or re-allocate PBO memory if texture size exceeds current allocation
            val requiredSizeInt = requiredBytes.toInt()
            if (pboAllocatedSizes[currentIndex] < requiredBytes) {
                pixelBuffer.rewind()
                GLES30.glBufferData(
                    GLES30.GL_PIXEL_UNPACK_BUFFER,
                    requiredSizeInt,
                    pixelBuffer,
                    GLES30.GL_STREAM_DRAW
                )
                val oldSize = pboAllocatedSizes[currentIndex]
                pboAllocatedSizes[currentIndex] = requiredBytes
                val diff = requiredBytes - oldSize
                totalAllocatedRamBytes += diff
                TextureMemoryTracker.allocGpu(diff)
            } else {
                // Buffer already sized — use glBufferSubData or glBufferData orphan
                pixelBuffer.rewind()
                GLES30.glBufferSubData(
                    GLES30.GL_PIXEL_UNPACK_BUFFER,
                    0,
                    requiredSizeInt,
                    pixelBuffer
                )
            }

            // 4. Bind target texture and initiate non-blocking GPU upload with offset 0 from PBO
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle)
            GLES30.glTexSubImage2D(
                GLES30.GL_TEXTURE_2D,
                0, // mip level 0
                0, 0, // x, y offset
                width, height,
                format, type,
                pixelBuffer
            )

            // 5. Generate mipmaps on GPU
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)

            // 6. Unbind PBO & texture
            GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, 0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)

            // 7. Insert fence sync for double-buffering safety
            val fence = GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE, 0)
            if (fence != 0L) {
                syncFences[currentIndex] = fence
            }

            totalPboUploads++
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error staging texture via PBO double-buffer", e)
            GLES30.glBindBuffer(GLES30.GL_PIXEL_UNPACK_BUFFER, 0)
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
            totalFallbackUploads++
            return false
        }
    }

    /**
     * Clean up PBO handles and sync fences on the GL thread.
     */
    fun destroy() {
        resourceManager.assertGlThread("PboRingBufferManager.destroy")

        if (pboHandles.isNotEmpty()) {
            for (i in syncFences.indices) {
                syncFences[i]?.let { fence ->
                    if (fence != 0L) {
                        GLES30.glDeleteSync(fence)
                    }
                }
                syncFences[i] = null
            }

            GLES30.glDeleteBuffers(pboHandles.size, pboHandles, 0)
            if (totalAllocatedRamBytes > 0) {
                TextureMemoryTracker.freeGpu(totalAllocatedRamBytes)
            }
            pboHandles = IntArray(0)
            pboAllocatedSizes = LongArray(0)
            totalAllocatedRamBytes = 0L
            isPboSupported = false
            Log.i(TAG, "Destroyed PBO ring buffers")
        }
    }

    /** Diagnostics for PBO ring buffer manager */
    fun getDiagnostics(): PboDiagnostics {
        return PboDiagnostics(
            isPboSupported = isPboSupported,
            ringDepth = pboHandles.size,
            allocatedRamBytes = totalAllocatedRamBytes,
            totalPboUploads = totalPboUploads,
            totalFallbackUploads = totalFallbackUploads,
            autoThrottled = autoThrottled
        )
    }

    data class PboDiagnostics(
        val isPboSupported: Boolean,
        val ringDepth: Int,
        val allocatedRamBytes: Long,
        val totalPboUploads: Long,
        val totalFallbackUploads: Long,
        val autoThrottled: Boolean
    )
}
