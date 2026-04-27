package com.linkpoint.render.lumiya.glres

import android.graphics.Bitmap
import android.opengl.GLES32
import android.opengl.GLUtils
import android.util.Log
import android.util.LruCache
import com.linkpoint.assets.TextureFormatPolicy
import java.util.UUID

/**
 * LRU texture cache backed by GL texture handles.
 *
 * Design lineage: Lumiya `GLTextureCache.java` / `GLTerrainTextureCache.java`,
 * modernised with Android `LruCache` and immutable-format textures.
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

    private val cache = object : LruCache<UUID, TextureEntry>(maxEntries) {
        override fun entryRemoved(evicted: Boolean, key: UUID?, oldValue: TextureEntry?, newValue: TextureEntry?) {
            if (evicted && oldValue != null) {
                resourceManager.deleteTexture(oldValue.handle)
                resourceManager.removeMemory(oldValue.sizeBytes)
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
        // If already cached, return existing
        cache.get(id)?.let { return it.handle }

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
        GLUtils.texSubImage2D(GLES32.GL_TEXTURE_2D, 0, 0, 0, bitmap)
        GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)

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
        cache.put(id, TextureEntry(handle, bitmap.width, bitmap.height, sizeBytes))

        return handle
    }

    fun remove(id: UUID) {
        cache.remove(id)
    }

    fun clear() {
        cache.evictAll()
    }

    val size: Int get() = cache.size()

    data class TextureEntry(
        val handle: Int,
        val width: Int,
        val height: Int,
        val sizeBytes: Long
    )
}
