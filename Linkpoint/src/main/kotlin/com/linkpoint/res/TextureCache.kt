package com.linkpoint.res
import java.util.*

import android.util.Log
import android.util.LruCache
import com.linkpoint.memory.MemoryManager
import com.linkpoint.memory.MemoryPressureListener

/**
 * Texture cache that integrates with MemoryManager for proper memory management
 */
class TextureCache : MemoryPressureListener {
    private const val TAG: String = "TextureCache"
    private const val DEFAULT_CACHE_SIZE_MB: Int = 32
    
    private val MemoryManager memoryManager
    private val LruCache<String, CachedTexture> cache
    
    public TextureCache(MemoryManager memoryManager) {
        this.memoryManager = memoryManager
        
        // Calculate cache size based on available memory
        val runtime: Runtime = Runtime.getRuntime()
        val maxMemory: Long = runtime.maxMemory()
        val cacheSize: Int = (Int) (maxMemory / 8); // Use 1/8 of available memory
        
        this.cache = LruCache<String, CachedTexture>(cacheSize) {
            override protected Int sizeOf(String key, CachedTexture texture) {
                return texture.getEstimatedSize()
            }
            
            override protected Unit entryRemoved(Boolean evicted, String key, 
                                       CachedTexture oldValue, CachedTexture newValue) {
                if (oldValue != null) {
                    memoryManager.trackDeallocation("texture_" + key, oldValue.getEstimatedSize())
                    oldValue.release()
                }
            }
        }
        
        memoryManager.addMemoryPressureListener(this)
    }
    
    fun put(key: String, texture: CachedTexture) {
        if (texture != null) {
            memoryManager.trackAllocation("texture_" + key, texture, texture.getEstimatedSize())
            cache.put(key, texture)
        }
    }
    
     public fun get(key: String): CachedTexture {
        return cache.get(key)
    }
    
    fun remove(key: String) {
        val texture: CachedTexture = cache.remove(key)
        if (texture != null) {
            memoryManager.trackDeallocation("texture_" + key, texture.getEstimatedSize())
            texture.release()
        }
    }
    
     public fun getCacheSize(): Int {
        return cache.size()
    }
    
    fun clear() {
        cache.evictAll()
    }
    
    override Unit onMemoryPressure() {
        Log.i(TAG, "Memory pressure detected, trimming texture cache")
        // Trim to 50% of current size
        cache.trimToSize(cache.size() / 2)
    }
}