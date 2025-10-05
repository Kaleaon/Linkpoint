package com.lumiyaviewer.lumiya.memory

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

class MemoryManager(private val context: Context) {
    private val activityManager: ActivityManager = 
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val totalAllocated = AtomicLong(0)
    private val resourceCache = ConcurrentHashMap<String, WeakReference<Any>>()
    private val listeners = CopyOnWriteArrayList<MemoryPressureListener>()
    
    fun addMemoryPressureListener(listener: MemoryPressureListener) {
        listeners.add(listener)
    }
    
    fun removeMemoryPressureListener(listener: MemoryPressureListener) {
        listeners.remove(listener)
    }
    
    fun trackAllocation(key: String, resource: Any, size: Long) {
        resourceCache[key] = WeakReference(resource)
        totalAllocated.addAndGet(size)
        checkMemoryPressure()
    }
    
    fun trackDeallocation(key: String, size: Long) {
        resourceCache.remove(key)
        totalAllocated.addAndGet(-size)
    }
    
    private fun checkMemoryPressure() {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        val availableMemory = memInfo.availMem
        val totalMemory = memInfo.totalMem
        val usedMemory = totalMemory - availableMemory
        
        val memoryUsagePercent = usedMemory.toFloat() / totalMemory
        
        if (memoryUsagePercent > 0.8f) {
            Log.w(TAG, "High memory usage detected: ${memoryUsagePercent * 100}%")
            performMemoryCleanup()
        }
    }
    
    fun performMemoryCleanup() {
        Log.i(TAG, "Performing memory cleanup")
        
        // Clean up weak references
        resourceCache.entries.removeIf { it.value.get() == null }
        
        // Force garbage collection
        System.gc()
        
        // Notify listeners about memory pressure
        notifyMemoryPressureListeners()
    }
    
    private fun notifyMemoryPressureListeners() {
        for (listener in listeners) {
            try {
                listener.onMemoryPressure()
            } catch (e: Exception) {
                Log.e(TAG, "Error notifying memory pressure listener", e)
            }
        }
    }
    
    fun getTotalAllocatedMemory(): Long = totalAllocated.get()
    
    fun getCachedResourceCount(): Int = resourceCache.size
    
    fun getMemoryUsagePercent(): Float {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        val availableMemory = memInfo.availMem
        val totalMemory = memInfo.totalMem
        val usedMemory = totalMemory - availableMemory
        
        return usedMemory.toFloat() / totalMemory
    }
    
    companion object {
        private const val TAG = "MemoryManager"
        private const val MB = 1024 * 1024L
    }
}
