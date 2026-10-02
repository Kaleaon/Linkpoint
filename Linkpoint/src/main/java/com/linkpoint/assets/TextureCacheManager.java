package com.linkpoint.assets;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TextureCacheManager provides LRU caching and accurate VRAM/RAM byte
 * accounting for compressed (ETC2/ETC1) and uncompressed (RGBA8) textures.
 */
public class TextureCacheManager {

    private static final String TAG = "TextureCacheManager";
    private static final int DEFAULT_MAX_CACHE_SIZE_BYTES = 64 * 1024 * 1024; // 64MB default

    private final int maxSizeBytes;
    private int currentSizeBytes = 0;
    private int evictionCount = 0;

    private final LinkedHashMap<String, TextureDecoder.TranscodedTexture> cache =
            new LinkedHashMap<String, TextureDecoder.TranscodedTexture>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, TextureDecoder.TranscodedTexture> eldest) {
                    if (currentSizeBytes > maxSizeBytes) {
                        try { Log.d(TAG, "LRU Evicting texture " + eldest.getKey() + " (size: " + eldest.getValue().getVramSizeBytes() + " bytes)"); } catch (Throwable ignored) {}
                        return true;
                    }
                    return false;
                }
            };

    public TextureCacheManager() {
        this(DEFAULT_MAX_CACHE_SIZE_BYTES);
    }

    public TextureCacheManager(int maxSizeBytes) {
        this.maxSizeBytes = maxSizeBytes;
    }

    /**
     * Put a transcoded texture into LRU cache with compressed byte accounting.
     */
    public synchronized void put(String key, TextureDecoder.TranscodedTexture texture) {
        if (key == null || texture == null) {
            return;
        }

        TextureDecoder.TranscodedTexture existing = cache.get(key);
        if (existing != null) {
            currentSizeBytes -= existing.getVramSizeBytes();
        }

        int newTextureSizeBytes = texture.getVramSizeBytes();
        evictToFit(newTextureSizeBytes);

        cache.put(key, texture);
        currentSizeBytes += newTextureSizeBytes;

        try { Log.d(TAG, "Cached texture " + key + " (" + texture.getWidth() + "x" + texture.getHeight() +
                ", format=" + texture.getFormat() + ", size=" + newTextureSizeBytes + " bytes). Total cache size: " +
                currentSizeBytes + "/" + maxSizeBytes + " bytes"); } catch (Throwable ignored) {}
    }

    /**
     * Get a texture from cache.
     */
    public synchronized TextureDecoder.TranscodedTexture get(String key) {
        if (key == null) return null;
        return cache.get(key);
    }

    /**
     * Remove a texture from cache.
     */
    public synchronized TextureDecoder.TranscodedTexture remove(String key) {
        if (key == null) return null;
        TextureDecoder.TranscodedTexture texture = cache.remove(key);
        if (texture != null) {
            currentSizeBytes -= texture.getVramSizeBytes();
        }
        return texture;
    }

    /**
     * Evict entries until there is enough room for incoming bytes.
     */
    public synchronized void evictToFit(int neededBytes) {
        while (currentSizeBytes + neededBytes > maxSizeBytes && !cache.isEmpty()) {
            Map.Entry<String, TextureDecoder.TranscodedTexture> eldest = cache.entrySet().iterator().next();
            String key = eldest.getKey();
            TextureDecoder.TranscodedTexture texture = eldest.getValue();
            cache.remove(key);
            currentSizeBytes -= texture.getVramSizeBytes();
            evictionCount++;
            try { Log.i(TAG, "LRU Evicted texture " + key + " (" + texture.getVramSizeBytes() + " bytes)"); } catch (Throwable ignored) {}
        }
    }

    /**
     * Clear all cached textures.
     */
    public synchronized void clear() {
        cache.clear();
        currentSizeBytes = 0;
    }

    public synchronized int getCurrentSizeBytes() {
        return currentSizeBytes;
    }

    public int getMaxSizeBytes() {
        return maxSizeBytes;
    }

    public synchronized int getItemCount() {
        return cache.size();
    }

    public synchronized int getEvictionCount() {
        return evictionCount;
    }

    /**
     * Calculate byte accounting size for a given resolution and format.
     */
    public static int calculateSizeBytes(int width, int height, TextureDecoder.TextureFormat format) {
        return TextureDecoder.calculateVramSizeBytes(width, height, format);
    }
}
