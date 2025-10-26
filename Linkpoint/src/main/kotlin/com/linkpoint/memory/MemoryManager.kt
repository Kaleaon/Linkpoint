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
    
    fun addMemoryPressureListener(listener: MemoryPressureListener) {
        listeners.add(listener)
    }
    
    fun removeMemoryPressureListener(listener: MemoryPressureListener) {
        listeners.remove(listener)
    }
    
    fun trackAllocation(key: String, resource: Object, size: Long) {
        resourceCache.put(key, WeakReference<>(resource))
        totalAllocated.addAndGet(size)
        
        checkMemoryPressure()
    }
    
    fun trackDeallocation(key: String, size: Long) {
        resourceCache.remove(key)
        totalAllocated.addAndGet(-size)
    }
    
     private fun checkMemoryPressure() {
        ActivityManager.MemoryInfo memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        val availableMemory: Long = memInfo.availMem
        val totalMemory: Long = memInfo.totalMem
        val usedMemory: Long = totalMemory - availableMemory
        
        val memoryUsagePercent: Float = (Float) usedMemory / totalMemory
        
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
    
     private fun notifyMemoryPressureListeners() {
        for (MemoryPressureListener listener : listeners) {
            try {
                listener.onMemoryPressure()
            } catch (Exception e) {
                Log.e(TAG, "Error notifying memory pressure listener", e)
            }
        }
    }
    
     public fun getTotalAllocatedMemory(): Long {
        return totalAllocated.get()
    }
    
     public fun getCachedResourceCount(): Int {
        return resourceCache.size()
    }
    
     public fun getMemoryUsagePercent(): Float {
        ActivityManager.MemoryInfo memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        val availableMemory: Long = memInfo.availMem
        val totalMemory: Long = memInfo.totalMem
        val usedMemory: Long = totalMemory - availableMemory
        
        return (Float) usedMemory / totalMemory
    }
}