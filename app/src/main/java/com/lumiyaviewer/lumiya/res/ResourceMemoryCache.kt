package com.lumiyaviewer.lumiya.res

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.memory.MemoryPressureListener
import java.util.Set

abstract class ResourceMemoryCache<ResourceParams, ResourceType> extends ResourceManager<ResourceParams, ResourceType> implements MemoryPressureListener {
    private Cache<ResourceParams, ResourceType> finalResults
    private Cache<ResourceParams, ResourceType> intermediateResults
    private MemoryManager memoryManager
    
    constructor(memoryManager: MemoryManager) {
        this.memoryManager = memoryManager
        this.finalResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        String key = "final_" + String.valueOf(notification.getKey().hashCode());
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()))
                    }
                })
                .build()
        
        this.intermediateResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        String key = "intermediate_" + String.valueOf(notification.getKey().hashCode());
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()))
                    }
                })
                .build()
                
        memoryManager.addMemoryPressureListener(this)
    }
    
    protected abstract fun estimateSize(resource: ResourceType): Long

    fun CompleteRequest(resourceparams: ResourceParams, resourcetype: ResourceType, set: Set<ResourceConsumer>): Unit {
        if (resourcetype != null) {
            String key = "final_" + String.valueOf(resourceparams.hashCode());
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype))
            this.finalResults.put(resourceparams, resourcetype)
        } else {
            this.finalResults.invalidate(resourceparams)
        }
        super.CompleteRequest(resourceparams, resourcetype, set)
    }

    fun IntermediateResult(resourceparams: ResourceParams, resourcetype: ResourceType, set: Set<ResourceConsumer>): Unit {
        if (resourcetype != null) {
            String key = "intermediate_" + String.valueOf(resourceparams.hashCode());
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype))
            this.intermediateResults.put(resourceparams, resourcetype)
        } else {
            this.intermediateResults.invalidate(resourceparams)
        }
        super.IntermediateResult(resourceparams, resourcetype, set)
    }

    fun RequestResource(resourceparams: ResourceParams, resourceConsumer: ResourceConsumer): Unit {
        ResourceType ifPresent = this.finalResults.getIfPresent(resourceparams)
        if (ifPresent != null) {
            resourceConsumer.OnResourceReady(ifPresent, false)
            return
        }
        ResourceType ifPresent2 = this.intermediateResults.getIfPresent(resourceparams)
        if (ifPresent2 != null) {
            resourceConsumer.OnResourceReady(ifPresent2, true)
        }
        super.RequestResource(resourceparams, resourceConsumer)
    }
    
    override fun onMemoryPressure(): Unit {
        // Clear intermediate results first as they're less critical
        intermediateResults.invalidateAll()
        
        // Trim results cache by 50%
        long currentSize = finalResults.size()
        if (currentSize > 10) { // Only trim if we have a reasonable number of items
            finalResults.asMap().entrySet().removeIf(entry -> 
                entry.getKey().hashCode() % 2 == 0); // Remove roughly half
        }
    }
    
    fun getCacheSize(): Long {
        return finalResults.size() + intermediateResults.size()
    }
}
