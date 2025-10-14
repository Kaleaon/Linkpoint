package com.lumiyaviewer.lumiya.res
import java.util.*

import android.util.Log
import android.util.LruCache
import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.memory.MemoryPressureListener

/**
 * Texture cache that integrates with MemoryManager for proper memory management
 */
class TextureCache : MemoryPressureListener {
    private String TAG = "TextureCache";
    private Int DEFAULT_CACHE_SIZE_MB = 32
    
    private MemoryManager memoryManager
    private LruCache<String, CachedTexture> cache
    
    constructor(memoryManager: MemoryManager) {
        this.memoryManager = memoryManager
        
        // Calculate cache size based on available memory
        Runtime runtime = Runtime.getRuntime()
        Long maxMemory = runtime.maxMemory()
        Int cacheSize = (Int) (maxMemory / 8); // Use 1/8 of available memory
        
        this.cache = LruCache<String, CachedTexture>(cacheSize) {
            override protected fun sizeOf(key: String, texture: CachedTexture): Int {
                return texture.getEstimatedSize()
            }
            
            override protected fun entryRemoved(evicted: Boolean, key: String, oldValue: CachedTexture, newValue: CachedTexture): Unit {
                if (oldValue != null) {
                    memoryManager.trackDeallocation("texture_" + key, oldValue.getEstimatedSize());
                    oldValue.release()
                }
            }
        }
        
        memoryManager.addMemoryPressureListener(this)
    }
    
    fun put(key: String, texture: CachedTexture): Unit {
        if (texture != null) {
            memoryManager.trackAllocation("texture_" + key, texture, texture.getEstimatedSize());
            cache.put(key, texture)
        }
    }
    
    fun get(key: String): CachedTexture {
        return cache.get(key)
    }
    
    fun remove(key: String): Unit {
        CachedTexture texture = cache.remove(key)
        if (texture != null) {
            memoryManager.trackDeallocation("texture_" + key, texture.getEstimatedSize());
            texture.release()
        }
    }
    
    fun getCacheSize(): Int {
        return cache.size()
    }
    
    fun clear(): Unit {
        cache.evictAll()
    }
    
    override fun onMemoryPressure(): Unit {
        Log.i(TAG, "Memory pressure detected, trimming texture cache");
        // Trim to 50% of current size
        cache.trimToSize(cache.size() / 2)
    }
}
