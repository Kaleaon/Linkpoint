package com.lumiyaviewer.lumiya.memory;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CopyOnWriteArrayList;

public class MemoryManager {
    private static final String TAG = "MemoryManager";
    private static final long MB = 1024 * 1024;
    
    private final Context context;
    private final ActivityManager activityManager;
    private final AtomicLong totalAllocated = new AtomicLong(0);
    private final ConcurrentHashMap<String, WeakReference<Object>> resourceCache = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<MemoryPressureListener> listeners = new CopyOnWriteArrayList<>();
    
    public MemoryManager(Context context) {
        this.context = context;
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }
    
    public void addMemoryPressureListener(MemoryPressureListener listener) {
        listeners.add(listener);
    }
    
    public void removeMemoryPressureListener(MemoryPressureListener listener) {
        listeners.remove(listener);
    }
    
    public void trackAllocation(String key, Object resource, long size) {
        resourceCache.put(key, new WeakReference<>(resource));
        totalAllocated.addAndGet(size);
        
        checkMemoryPressure();
    }
    
    public void trackDeallocation(String key, long size) {
        resourceCache.remove(key);
        totalAllocated.addAndGet(-size);
    }
    
    private void checkMemoryPressure() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);
        
        long availableMemory = memInfo.availMem;
        long totalMemory = memInfo.totalMem;
        long usedMemory = totalMemory - availableMemory;
        
        float memoryUsagePercent = (float) usedMemory / totalMemory;
        
        if (memoryUsagePercent > 0.8f) {
            Log.w(TAG, "High memory usage detected: " + (memoryUsagePercent * 100) + "%");
            performMemoryCleanup();
        }
    }
    
    public void performMemoryCleanup() {
        Log.i(TAG, "Performing memory cleanup");
        
        // Clean up weak references
        resourceCache.entrySet().removeIf(entry -> entry.getValue().get() == null);
        
        // Force garbage collection
        System.gc();
        
        // Notify listeners about memory pressure
        notifyMemoryPressureListeners();
    }
    
    private void notifyMemoryPressureListeners() {
        for (MemoryPressureListener listener : listeners) {
            try {
                listener.onMemoryPressure();
            } catch (Exception e) {
                Log.e(TAG, "Error notifying memory pressure listener", e);
            }
        }
    }
    
    public long getTotalAllocatedMemory() {
        return totalAllocated.get();
    }
    
    public int getCachedResourceCount() {
        return resourceCache.size();
    }
    
    public float getMemoryUsagePercent() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);
        
        long availableMemory = memInfo.availMem;
        long totalMemory = memInfo.totalMem;
        long usedMemory = totalMemory - availableMemory;
        
        return (float) usedMemory / totalMemory;
    }
}