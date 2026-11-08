package com.linkpoint.render.lumiya.glres

import android.graphics.Bitmap
import android.opengl.GLES32
import android.opengl.GLUtils
import android.util.Log
import android.util.LruCache
import com.linkpoint.assets.TextureFormatPolicy
import com.linkpoint.assets.TextureMemoryTracker
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * LRU texture cache backed by GL texture handles.
 *
 * Design lineage: Lumiya `GLTextureCache.java` / `GLTerrainTextureCache.java`,
 * modernised with Android `LruCache`, immutable-format textures, and PBO double-buffered uploads.
 *
 * Textures are keyed by SL asset UUID.  When evicted the GL handle is
 * returned to the resource manager for deferred deletion.
 */
class GLTextureCache(
    private val resourceManager: GLResourceManager,
    maxEntries: Int = 512
) {

    companion object {
        private const val TAG = "GLTextureCache"
    }

    val pboManager = PboRingBufferManager(resourceManager)

    private val cache = object : LruCache<UUID, TextureEntry>(maxEntries) {
        override fun entryRemoved(evicted: Boolean, key: UUID?, oldValue: TextureEntry?, newValue: TextureEntry?) {
            if (evicted && oldValue != null) {
                resourceManager.deleteTexture(oldValue.handle)
                resourceManager.removeMemory(oldValue.sizeBytes)
                // Keep TextureMemoryTracker in sync with the live GL set.
                // Without this, the HUD/debug-report Live Textures counter
                // (LinkpointTexture-pipeline only) shows zero even when
                // GLTextureCache is full of uploaded textures.
                TextureMemoryTracker.freeGpu(oldValue.sizeBytes)
                TextureMemoryTracker.textureClosed()
            }
        }
    }

    /** Get a cached texture handle, or 0 if not present. */
    fun get(id: UUID): Int {
        return cache.get(id)?.handle ?: 0
    }

    /** Upload a bitmap and cache the resulting texture. */
    fun put(
        id: UUID,
        bitmap: Bitmap,
        semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO
    ): Int {
        return putOrUpdate(id, bitmap, semantic, isPlaceholder = false)
    }

    /**
     * Upload a bitmap and cache or update the resulting texture.
     * Swaps placeholder textures with high-res textures without deleting/rebuilding
     * shader programs or vertex buffers.
     */
    fun putOrUpdate(
        id: UUID,
        bitmap: Bitmap,
        semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO,
        isPlaceholder: Boolean = false
    ): Int {
        resourceManager.assertGlThread("GLTextureCache.putOrUpdate")
        val existing = cache.get(id)
        if (existing != null) {
            if (existing.isPlaceholder && !isPlaceholder) {
                // Delete old placeholder handle to release GPU memory
                resourceManager.deleteTexture(existing.handle)
                resourceManager.removeMemory(existing.sizeBytes)
                TextureMemoryTracker.freeGpu(existing.sizeBytes)
                cache.remove(id)
            } else if (!existing.isPlaceholder || isPlaceholder) {
                return existing.handle
            }
        }

        val handle = resourceManager.createTexture()
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, handle)

        // Use immutable storage where possible (GL ES 3.0+)
        GLES32.glTexStorage2D(
            GLES32.GL_TEXTURE_2D,
            TextureFormatPolicy.mipLevelsFor(bitmap.width, bitmap.height),
            if (semantic == TextureFormatPolicy.TextureSemantic.ALBEDO) GLES32.GL_SRGB8_ALPHA8 else GLES32.GL_RGBA8,
            bitmap.width,
            bitmap.height
        )
        val uploadedViaPbo = if (pboManager.isPboSupported) {
            try {
                val byteBuffer = ByteBuffer.allocateDirect(bitmap.width * bitmap.height * 4).order(ByteOrder.nativeOrder())
                bitmap.copyPixelsToBuffer(byteBuffer)
                byteBuffer.rewind()
                pboManager.stageAndUploadTexture(handle, bitmap.width, bitmap.height, byteBuffer)
            } catch (e: Exception) {
                Log.w(TAG, "PBO buffer copy failed, falling back to direct upload", e)
                false
            }
        } else false

        if (!uploadedViaPbo) {
            GLUtils.texSubImage2D(GLES32.GL_TEXTURE_2D, 0, 0, 0, bitmap)
            GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)
        }

        // Trilinear filtering
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR_MIPMAP_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT)

        // Anisotropic filtering if available
        val maxAniso = FloatArray(1)
        GLES32.glGetFloatv(0x84FF /* GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT */, maxAniso, 0)
        if (maxAniso[0] > 1.0f) {
            GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D, 0x84FE /* GL_TEXTURE_MAX_ANISOTROPY_EXT */, minOf(maxAniso[0], 4.0f))
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)

        val sizeBytes = (bitmap.width * bitmap.height * 4 * 4L / 3L) // approximate with mipmaps
        resourceManager.addMemory(sizeBytes)
        cache.put(id, TextureEntry(handle, bitmap.width, bitmap.height, sizeBytes, isPlaceholder))
        // Mirror the GL upload into TextureMemoryTracker so the HUD /
        // debug-report counters reflect the actual live GPU set rather
        // than the LinkpointTexture-only side path. Paired with the
        // textureClosed/freeGpu calls in entryRemoved above.
        TextureMemoryTracker.allocGpu(sizeBytes)
        TextureMemoryTracker.textureOpened()

        return handle
    }

    fun remove(id: UUID) {
        resourceManager.assertGlThread("GLTextureCache.remove")
        cache.remove(id)
    }

    fun clear() {
        resourceManager.assertGlThread("GLTextureCache.clear")
        cache.evictAll()
    }

    val size: Int get() = cache.size()

    data class TextureEntry(
        val handle: Int,
        val width: Int,
        val height: Int,
        val sizeBytes: Long,
        val isPlaceholder: Boolean = false
    )
}
