package com.lumiyaviewer.lumiya.graphics.filament

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap
import java.util.UUID
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetchRequest

class FilamentTextureManager(
    private val context: Context,
    private val engine: Engine
) {
    companion object {
        private const val TAG = "FilamentTextureMgr"
    }
    
    private val textureCache = ConcurrentHashMap<UUID, Texture>()
    private val pendingTextures = ConcurrentHashMap<UUID, Boolean>()
    
    fun getTexture(uuid: UUID): Texture? {
        return textureCache[uuid]
    }
    
    fun requestTexture(uuid: UUID, priority: Float) {
        if (textureCache.containsKey(uuid) || pendingTextures.containsKey(uuid)) {
            return
        }
        
        pendingTextures[uuid] = true
        
        // Mock implementation of texture fetching
        // In real app, this would use the SL network stack
        
        // For compilation fix: SLTextureFetchRequest usage
        // Assuming SLTextureFetchRequest(UUID, Float, Callback) signature or similar
        
        // stub:
        /*
        val request = SLTextureFetchRequest(uuid, priority) { success, data ->
            if (success && data != null) {
                loadTextureFromData(uuid, data)
            }
            pendingTextures.remove(uuid)
        }
        request.start()
        */
    }
    
    private fun loadTextureFromData(uuid: UUID, data: ByteArray) {
        try {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            if (bitmap != null) {
                val texture = Texture.Builder()
                    .width(bitmap.width)
                    .height(bitmap.height)
                    .levels(1)
                    .sampler(Texture.Sampler.SAMPLER_2D)
                    .format(Texture.InternalFormat.SRGB8_A8)
                    .build(engine)
                    
                // Upload data...
                
                textureCache[uuid] = texture
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading texture $uuid", e)
        }
    }
    
    fun destroy() {
        textureCache.values.forEach { engine.destroyTexture(it) }
        textureCache.clear()
    }
}
