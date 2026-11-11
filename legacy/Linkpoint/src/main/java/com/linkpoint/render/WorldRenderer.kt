package com.linkpoint.render

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * WorldRenderer orchestrates 3D scene rendering and VRAM texture management.
 *
 * Integrates [GpuTextures] for memory-budgeted LRU texture caching and registers
 * Android ComponentCallbacks2 memory pressure listeners.
 *
 * During frame rendering, visible textures in the camera frustum are pinned in [gpuTextures]
 * to protect them from eviction. If previously evicted textures re-enter the frustum,
 * [refreshTextures] re-requests them from [TextureFetcher] and re-uploads them cleanly.
 */
class WorldRenderer(
    private val context: Context? = null,
    private val engine: Engine? = null,
    val gpuTextures: GpuTextures = GpuTextures(context, engine)
) : ComponentCallbacks2 {

    companion object {
        private const val TAG = "WorldRenderer"

        private fun logI(msg: String) { runCatching { Log.i(TAG, msg) } }
        private fun logW(msg: String) { runCatching { Log.w(TAG, msg) } }
        private fun logD(msg: String) { runCatching { Log.d(TAG, msg) } }
        private fun logE(msg: String, e: Throwable? = null) { runCatching { Log.e(TAG, msg, e) } }
    }

    fun interface TextureFetcher {
        fun fetch(textureId: UUID, onResolved: (Bitmap?) -> Unit)
    }

    private var renderThreadExecutor: ((Runnable) -> Unit)? = null
    @Volatile private var isRegistered = false

    init {
        bindContext(context)
    }

    fun setRenderThreadExecutor(executor: (Runnable) -> Unit) {
        this.renderThreadExecutor = executor
        gpuTextures.setRenderThreadExecutor(executor)
    }

    fun bindContext(ctx: Context?) {
        if (!isRegistered && ctx != null) {
            try {
                ctx.applicationContext.registerComponentCallbacks(this)
                isRegistered = true
                logI("Registered ComponentCallbacks2 listener for WorldRenderer")
            } catch (e: Exception) {
                logW("Failed to register ComponentCallbacks2: ${e.message}")
            }
        }
    }

    fun unbindContext() {
        if (isRegistered && context != null) {
            try {
                context.applicationContext.unregisterComponentCallbacks(this)
                isRegistered = false
                logI("Unregistered ComponentCallbacks2 listener for WorldRenderer")
            } catch (e: Exception) {
                logW("Failed to unregister ComponentCallbacks2: ${e.message}")
            }
        }
        gpuTextures.unregisterComponentCallbacks()
    }

    /**
     * Called before rendering a frame. Pins all currently visible frustum textures
     * in [gpuTextures] to prevent LRU eviction during frame execution.
     */
    fun beginFrame(visibleTextureIds: List<UUID>) {
        gpuTextures.pinAll(visibleTextureIds)
    }

    /**
     * Called after rendering a frame. Unpins all frustum textures.
     */
    fun endFrame() {
        gpuTextures.unpinAll()
    }

    /**
     * Checks visible texture UUIDs. Re-requests and re-uploads any evicted or missing textures
     * from [fetcher] if they are visible in the frustum again.
     */
    fun refreshTextures(
        visibleTextureIds: List<UUID>,
        fetcher: TextureFetcher
    ) {
        for (id in visibleTextureIds) {
            val state = gpuTextures.getTextureState(id)
            if (state == GpuTextures.TextureState.EVICTED) {
                logD("Re-requesting evicted texture $id for frustum re-upload")
                fetcher.fetch(id) { bitmap ->
                    if (bitmap != null && !bitmap.isRecycled) {
                        uploadBitmapTexture(id, bitmap)
                    }
                }
            }
        }
    }

    /**
     * Uploads a decoded [Bitmap] as a Filament GPU texture into [gpuTextures].
     */
    fun uploadBitmapTexture(
        id: UUID,
        bitmap: Bitmap,
        isPinned: Boolean = false
    ): Boolean {
        val width = bitmap.width
        val height = bitmap.height
        var filamentTexture: Texture? = null

        if (engine != null) {
            try {
                filamentTexture = Texture.Builder()
                    .width(width)
                    .height(height)
                    .levels(1)
                    .format(Texture.InternalFormat.SRGB8_A8)
                    .sampler(Texture.Sampler.SAMPLER_2D)
                    .build(engine)

                val buffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder())
                bitmap.copyPixelsToBuffer(buffer)
                buffer.flip()

                val pixelBuffer = Texture.PixelBufferDescriptor(
                    buffer,
                    Texture.Format.RGBA,
                    Texture.Type.UBYTE
                )
                filamentTexture.setImage(engine, 0, pixelBuffer)
            } catch (e: Exception) {
                logE("Failed to create Filament Texture handle for $id: ${e.message}", e)
                return false
            }
        }

        return gpuTextures.uploadTexture(
            id = id,
            texture = filamentTexture,
            width = width,
            height = height,
            format = GpuTextures.TextureFormat.RGBA_8888,
            mipLevels = 1,
            isPinned = isPinned
        )
    }

    fun destroy() {
        unbindContext()
        gpuTextures.purgeUnpinned()
    }

    // --- ComponentCallbacks2 Implementation ---

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onTrimMemory(level: Int) {
        logI("WorldRenderer onTrimMemory received level=$level")
        gpuTextures.onTrimMemory(level)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onLowMemory() {
        logW("WorldRenderer onLowMemory received")
        gpuTextures.onLowMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // No-op
    }
}
