package com.linkpoint.memory

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.CopyOnWriteArrayList

class MemoryManager {
    private const val TAG: String = "MemoryManager"
    private const val MB: Long = 1024 * 1024
    
    private val Context context
    private val ActivityManager activityManager
    private val AtomicLong totalAllocated = AtomicLong(0)
    private val ConcurrentHashMap<String, WeakReference<Object>> resourceCache = ConcurrentHashMap<>()
    private val CopyOnWriteArrayList<MemoryPressureListener> listeners = CopyOnWriteArrayList<>()
    
    public MemoryManager(Context context) {
        this.context = context
        this.activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)
    }
    
    fun addMemoryPressureListener(MemoryPressureListener listener) {
        listeners.add(listener)
    }
    
    fun removeMemoryPressureListener(MemoryPressureListener listener) {
        listeners.remove(listener)
    }
    
    fun trackAllocation(String key, Object resource, Long size) {
        resourceCache.put(key, WeakReference<>(resource))
        totalAllocated.addAndGet(size)
        
        checkMemoryPressure()
    }
    
    fun trackDeallocation(String key, Long size) {
        resourceCache.remove(key)
        totalAllocated.addAndGet(-size)
    }
    
    private Unit checkMemoryPressure() {
        ActivityManager.MemoryInfo memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        Long availableMemory = memInfo.availMem
        Long totalMemory = memInfo.totalMem
        Long usedMemory = totalMemory - availableMemory
        
        Float memoryUsagePercent = (Float) usedMemory / totalMemory
        
        if (memoryUsagePercent > 0.8f) {
            Log.w(TAG, "High memory usage detected: " + (memoryUsagePercent * 100) + "%")
            performMemoryCleanup()
        }
    }
    
    fun performMemoryCleanup() {
        Log.i(TAG, "Performing memory cleanup")
        
        // Clean up weak references
        resourceCache.entrySet().removeIf(entry -> entry.getValue().get() == null)
        
        // Force garbage collection
        System.gc()
        
        // Notify listeners about memory pressure
        notifyMemoryPressureListeners()
    }
    
    private Unit notifyMemoryPressureListeners() {
        for (MemoryPressureListener listener : listeners) {
            try {
                listener.onMemoryPressure()
            } catch (Exception e) {
                Log.e(TAG, "Error notifying memory pressure listener", e)
            }
        }
    }
    
    public Long getTotalAllocatedMemory() {
        return totalAllocated.get()
    }
    
    public Int getCachedResourceCount() {
        return resourceCache.size()
    }
    
    public Float getMemoryUsagePercent() {
        ActivityManager.MemoryInfo memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        Long availableMemory = memInfo.availMem
        Long totalMemory = memInfo.totalMem
        Long usedMemory = totalMemory - availableMemory
        
        return (Float) usedMemory / totalMemory
    }
}