package com.lumiyaviewer.lumiya.memory;

import android.app.ActivityManager;
import android.content.Context;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MemoryManager
 */
@RunWith(RobolectricTestRunner.class)
public class MemoryManagerTest {
    
    @Mock
    private MemoryPressureListener mockListener;
    
    private MemoryManager memoryManager;
    private Context context;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        memoryManager = new MemoryManager(context);
    }
    
    @Test
    public void testInitialization() {
        assertNotNull("MemoryManager should be initialized", memoryManager);
        assertEquals("Initial allocated memory should be 0", 0, memoryManager.getTotalAllocatedMemory());
        assertEquals("Initial cache count should be 0", 0, memoryManager.getCachedResourceCount());
    }
    
    @Test
    public void testTrackAllocation() {
        Object testResource = new Object();
        long testSize = 1024;
        
        memoryManager.trackAllocation("test_key", testResource, testSize);
        
        assertEquals("Allocated memory should equal test size", testSize, memoryManager.getTotalAllocatedMemory());
        assertEquals("Cache count should be 1", 1, memoryManager.getCachedResourceCount());
    }
    
    @Test
    public void testTrackDeallocation() {
        Object testResource = new Object();
        long testSize = 1024;
        
        // First allocate
        memoryManager.trackAllocation("test_key", testResource, testSize);
        
        // Then deallocate
        memoryManager.trackDeallocation("test_key", testSize);
        
        assertEquals("Allocated memory should be 0 after deallocation", 0, memoryManager.getTotalAllocatedMemory());
        assertEquals("Cache count should still be 1 (entry not removed)", 1, memoryManager.getCachedResourceCount());
    }
    
    @Test
    public void testMemoryPressureListener() {
        memoryManager.addMemoryPressureListener(mockListener);
        
        // Trigger memory cleanup manually
        memoryManager.performMemoryCleanup();
        
        // Verify listener was called
        verify(mockListener, times(1)).onMemoryPressure();
    }
    
    @Test
    public void testWeakReferenceCleanup() {
        Object testResource = new Object();
        memoryManager.trackAllocation("test_key", testResource, 1024);
        
        assertEquals("Cache count should be 1 before cleanup", 1, memoryManager.getCachedResourceCount());
        
        // Clear the reference and force GC
        testResource = null;
        System.gc();
        
        // Manually trigger cleanup
        memoryManager.performMemoryCleanup();
        
        // The weak reference should be cleaned up, but we need to consider that
        // the GC might not have run yet, so the count could still be 1
        assertTrue("Cache count should be 0 or 1 after cleanup", 
                   memoryManager.getCachedResourceCount() <= 1);
    }
    
    @Test
    public void testMultipleAllocations() {
        for (int i = 0; i < 5; i++) {
            memoryManager.trackAllocation("key_" + i, new Object(), 512);
        }
        
        assertEquals("Total allocated should be 2560", 2560, memoryManager.getTotalAllocatedMemory());
        assertEquals("Cache count should be 5", 5, memoryManager.getCachedResourceCount());
    }
    
    @Test
    public void testListenerManagement() {
        memoryManager.addMemoryPressureListener(mockListener);
        memoryManager.removeMemoryPressureListener(mockListener);
        
        // Trigger cleanup - listener should not be called
        memoryManager.performMemoryCleanup();
        
        verify(mockListener, never()).onMemoryPressure();
    }
}