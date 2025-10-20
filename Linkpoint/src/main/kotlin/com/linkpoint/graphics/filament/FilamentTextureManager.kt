package com.linkpoint.graphics.filament

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import com.google.android.filament.Texture.InternalFormat
import com.google.android.filament.Texture.PixelBufferDescriptor
import com.linkpoint.render.tex.TextureClass
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * FilamentTextureManager - Manages texture loading and caching for Filament
 * 
 * Handles:
 * - Loading textures from network/cache
 * - Converting Bitmap to Filament Texture
 * - Texture caching and lifecycle
 * - Integration with existing texture managers
 */
class FilamentTextureManager(
    private val context: Context,
    private val engine: Engine
) {
    companion object {
        private const val TAG = "FilamentTextureMgr"
        private const val MAX_TEXTURE_SIZE = 2048
    }
    
    // Texture cache
    private val textureCache = ConcurrentHashMap<UUID, Texture>()
    private val bitmapTextureCache = ConcurrentHashMap<String, Texture>()
    
    /**
     * Create a Filament texture from a Bitmap
     */
    fun createTextureFromBitmap(bitmap: Bitmap, srgb: Boolean = true): Texture {
        // Check cache first
        val cacheKey = "bitmap_${bitmap.width}x${bitmap.height}_${bitmap.hashCode()}"
        textureCache[cacheKey]?.let { return it }
        
        // Create texture
        val texture = Texture.Builder()
            .width(bitmap.width)
            .height(bitmap.height)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(if (srgb) InternalFormat.SRGB8_A8 else InternalFormat.RGBA8)
            .levels(1)
            .build(engine)
        
        // Convert bitmap to ByteBuffer
        val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(buffer)
        buffer.rewind()
        
        // Upload to texture
        val pixelBuffer = PixelBufferDescriptor(
            buffer,
            Texture.Format.RGBA,
            Texture.Type.UBYTE
        )
        
        texture.setImage(engine, 0, pixelBuffer)
        
        // Cache it
        bitmapTextureCache[cacheKey] = texture
        
        Log.d(TAG, "Created texture from bitmap: ${bitmap.width}x${bitmap.height}")
        
        return texture
    }
    
    /**
     * Load a texture by UUID (from SL texture system)
     */
    fun loadTexture(uuid: UUID): Texture? {
        // Check cache
        textureCache[uuid]?.let { return it }
        
        // TODO: Integrate with existing texture fetcher
        // For now, create a placeholder texture
        Log.d(TAG, "Loading texture $uuid (placeholder)")
        
        return createPlaceholderTexture()
    }
    
    /**
     * Create a placeholder texture (white/checkerboard)
     */
    fun createPlaceholderTexture(): Texture {
        val size = 64
        val texture = Texture.Builder()
            .width(size)
            .height(size)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(InternalFormat.RGBA8)
            .levels(1)
            .build(engine)
        
        // Create checkerboard pattern
        val pixels = ByteBuffer.allocateDirect(size * size * 4)
        for (y in 0 until size) {
            for (x in 0 until size) {
                val checker = ((x / 8) + (y / 8)) % 2
                val color = if (checker == 0) 255.toByte() else 200.toByte()
                pixels.put(color)
                pixels.put(color)
                pixels.put(color)
                pixels.put(255.toByte())
            }
        }
        pixels.rewind()
        
        val pixelBuffer = PixelBufferDescriptor(
            pixels,
            Texture.Format.RGBA,
            Texture.Type.UBYTE
        )
        
        texture.setImage(engine, 0, pixelBuffer)
        
        return texture
    }
    
    /**
     * Create a solid color texture
     */
    fun createSolidColorTexture(r: Int, g: Int, b: Int, a: Int = 255): Texture {
        val texture = Texture.Builder()
            .width(1)
            .height(1)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(InternalFormat.RGBA8)
            .levels(1)
            .build(engine)
        
        val pixels = ByteBuffer.allocateDirect(4)
            .put(r.toByte())
            .put(g.toByte())
            .put(b.toByte())
            .put(a.toByte())
        pixels.rewind()
        
        val pixelBuffer = PixelBufferDescriptor(
            pixels,
            Texture.Format.RGBA,
            Texture.Type.UBYTE
        )
        
        texture.setImage(engine, 0, pixelBuffer)
        
        return texture
    }
    
    /**
     * Generate mipmaps for a texture
     */
    fun generateMipmaps(texture: Texture) {
        texture.generateMipmaps(engine)
    }
    
    /**
     * Cleanup all textures
     */
    fun destroy() {
        Log.i(TAG, "Destroying ${textureCache.size + bitmapTextureCache.size} textures...")
        
        textureCache.values.forEach { texture ->
            try {
                engine.destroyTexture(texture)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying texture: ${e.message}")
            }
        }
        
        bitmapTextureCache.values.forEach { texture ->
            try {
                engine.destroyTexture(texture)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying texture: ${e.message}")
            }
        }
        
        textureCache.clear()
        bitmapTextureCache.clear()
        
        Log.i(TAG, "Texture manager destroyed")
    }
}
