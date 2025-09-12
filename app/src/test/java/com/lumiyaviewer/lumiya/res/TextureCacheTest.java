package com.lumiyaviewer.lumiya.res;

import android.content.Context;
import com.lumiyaviewer.lumiya.memory.MemoryManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

/**
 * Unit tests for TextureCache and CachedTexture
 */
@RunWith(RobolectricTestRunner.class)
public class TextureCacheTest {
    
    private MemoryManager memoryManager;
    private TextureCache textureCache;
    private Context context;
    
    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        memoryManager = new MemoryManager(context);
        textureCache = new TextureCache(memoryManager);
    }
    
    @Test
    public void testMemoryManager() {
        // This test matches the example from the problem statement
        assertNotNull("MemoryManager should be initialized", memoryManager);
        assertNotNull("TextureCache should be initialized", textureCache);
        
        CachedTexture texture = new CachedTexture(1, 512, 512, 0x1908);
        textureCache.put("test_texture", texture);
        
        assertEquals("Should retrieve the same texture", texture, textureCache.get("test_texture"));
        assertTrue("Cache size should be greater than 0", textureCache.getCacheSize() > 0);
    }
    
    @Test
    public void testCachedTextureCreation() {
        CachedTexture texture = new CachedTexture(123, 256, 256, 0x1908);
        
        assertEquals("Texture ID should match", 123, texture.getTextureId());
        assertEquals("Width should match", 256, texture.getWidth());
        assertEquals("Height should match", 256, texture.getHeight());
        assertEquals("Format should match", 0x1908, texture.getFormat());
        assertFalse("Texture should not be released initially", texture.isReleased());
    }
    
    @Test
    public void testCachedTextureEstimatedSize() {
        // Test RGBA texture (4 bytes per pixel)
        CachedTexture rgbaTexture = new CachedTexture(1, 100, 100, 0x1908);
        assertEquals("RGBA texture size should be 40000 bytes", 40000, rgbaTexture.getEstimatedSize());
        
        // Test RGB texture (3 bytes per pixel)
        CachedTexture rgbTexture = new CachedTexture(2, 100, 100, 0x1907);
        assertEquals("RGB texture size should be 30000 bytes", 30000, rgbTexture.getEstimatedSize());
        
        // Test Luminance texture (1 byte per pixel)
        CachedTexture lumTexture = new CachedTexture(3, 100, 100, 0x190A);
        assertEquals("Luminance texture size should be 10000 bytes", 10000, lumTexture.getEstimatedSize());
    }
    
    @Test
    public void testTextureCacheOperations() {
        CachedTexture texture1 = new CachedTexture(1, 128, 128, 0x1908);
        CachedTexture texture2 = new CachedTexture(2, 64, 64, 0x1907);
        
        // Test put and get
        textureCache.put("texture1", texture1);
        textureCache.put("texture2", texture2);
        
        assertEquals("Should retrieve texture1", texture1, textureCache.get("texture1"));
        assertEquals("Should retrieve texture2", texture2, textureCache.get("texture2"));
        assertEquals("Cache size should be 2", 2, textureCache.getCacheSize());
        
        // Test remove
        textureCache.remove("texture1");
        assertNull("Should not retrieve removed texture", textureCache.get("texture1"));
        assertEquals("Cache size should be 1 after removal", 1, textureCache.getCacheSize());
    }
    
    @Test
    public void testMemoryTracking() {
        CachedTexture texture = new CachedTexture(1, 100, 100, 0x1908);
        
        long initialMemory = memoryManager.getTotalAllocatedMemory();
        textureCache.put("test", texture);
        long afterAllocation = memoryManager.getTotalAllocatedMemory();
        
        assertTrue("Memory usage should increase after allocation", 
                   afterAllocation > initialMemory);
        
        textureCache.remove("test");
        long afterDeallocation = memoryManager.getTotalAllocatedMemory();
        
        assertTrue("Memory usage should decrease after deallocation", 
                   afterDeallocation < afterAllocation);
    }
    
    @Test
    public void testMemoryPressureResponse() {
        // Fill cache with textures
        for (int i = 0; i < 10; i++) {
            CachedTexture texture = new CachedTexture(i, 64, 64, 0x1908);
            textureCache.put("texture_" + i, texture);
        }
        
        int initialSize = textureCache.getCacheSize();
        
        // Simulate memory pressure
        textureCache.onMemoryPressure();
        
        assertTrue("Cache size should be reduced after memory pressure", 
                   textureCache.getCacheSize() < initialSize);
    }
    
    @Test
    public void testCacheClear() {
        CachedTexture texture1 = new CachedTexture(1, 64, 64, 0x1908);
        CachedTexture texture2 = new CachedTexture(2, 64, 64, 0x1908);
        
        textureCache.put("texture1", texture1);
        textureCache.put("texture2", texture2);
        
        assertEquals("Cache should have 2 items", 2, textureCache.getCacheSize());
        
        textureCache.clear();
        
        assertEquals("Cache should be empty after clear", 0, textureCache.getCacheSize());
    }
    
    @Test
    public void testTextureRelease() {
        CachedTexture texture = new CachedTexture(1, 64, 64, 0x1908);
        
        assertFalse("Texture should not be released initially", texture.isReleased());
        
        texture.release();
        
        assertTrue("Texture should be released after calling release()", texture.isReleased());
    }
}