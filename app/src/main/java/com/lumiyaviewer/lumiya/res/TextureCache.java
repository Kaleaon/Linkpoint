package com.lumiyaviewer.lumiya.res;
import java.util.*;

import android.util.Log;
import android.util.LruCache;
import com.lumiyaviewer.lumiya.memory.MemoryManager;
import com.lumiyaviewer.lumiya.memory.MemoryPressureListener;

/**
 * Texture cache that integrates with MemoryManager for proper memory management
 */
public class TextureCache implements MemoryPressureListener {
    private static final String TAG = "TextureCache";
    private static final int DEFAULT_CACHE_SIZE_MB = 32;
    
    private final MemoryManager memoryManager;
    private final LruCache<String, CachedTexture> cache;
    
    public TextureCache(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        
        // Calculate cache size based on available memory
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        int cacheSize = (int) (maxMemory / 8); // Use 1/8 of available memory
        
        this.cache = new LruCache<String, CachedTexture>(cacheSize) {
            @Override
            protected int sizeOf(String key, CachedTexture texture) {
                return texture.getEstimatedSize();
            }
            
            @Override
            protected void entryRemoved(boolean evicted, String key, 
                                       CachedTexture oldValue, CachedTexture newValue) {
                if (oldValue != null) {
                    memoryManager.trackDeallocation("texture_" + key, oldValue.getEstimatedSize());
                    oldValue.release();
                }
            }
        };
        
        memoryManager.addMemoryPressureListener(this);
    }
    
    public void put(String key, CachedTexture texture) {
        if (texture != null) {
            memoryManager.trackAllocation("texture_" + key, texture, texture.getEstimatedSize());
            cache.put(key, texture);
        }
    }
    
    public CachedTexture get(String key) {
        return cache.get(key);
    }
    
    public void remove(String key) {
        CachedTexture texture = cache.remove(key);
        if (texture != null) {
            memoryManager.trackDeallocation("texture_" + key, texture.getEstimatedSize());
            texture.release();
        }
    }
    
    public int getCacheSize() {
        return cache.size();
    }
    
    public void clear() {
        cache.evictAll();
    }
    
    @Override
    public void onMemoryPressure() {
        Log.i(TAG, "Memory pressure detected, trimming texture cache");
        // Trim to 50% of current size
        cache.trimToSize(cache.size() / 2);
    }
}