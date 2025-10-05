package com.linkpoint.res

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.linkpoint.memory.MemoryManager
import com.linkpoint.memory.MemoryPressureListener
import java.util.Set

abstract class ResourceMemoryCache<ResourceParams, ResourceType> : ResourceManager<ResourceParams, ResourceType> : MemoryPressureListener {
    private val Cache<ResourceParams, ResourceType> finalResults
    private val Cache<ResourceParams, ResourceType> intermediateResults
    private val MemoryManager memoryManager
    
    public ResourceMemoryCache(MemoryManager memoryManager) {
        this.memoryManager = memoryManager
        this.finalResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        String key = "final_" + String.valueOf(notification.getKey().hashCode())
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()))
                    }
                })
                .build()
        
        this.intermediateResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        String key = "intermediate_" + String.valueOf(notification.getKey().hashCode())
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()))
                    }
                })
                .build()
                
        memoryManager.addMemoryPressureListener(this)
    }
    
    protected abstract Long estimateSize(ResourceType resource)

    fun CompleteRequest(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        if (resourcetype != null) {
            String key = "final_" + String.valueOf(resourceparams.hashCode())
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype))
            this.finalResults.put(resourceparams, resourcetype)
        } else {
            this.finalResults.invalidate(resourceparams)
        }
        super.CompleteRequest(resourceparams, resourcetype, set)
    }

    fun IntermediateResult(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        if (resourcetype != null) {
            String key = "intermediate_" + String.valueOf(resourceparams.hashCode())
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype))
            this.intermediateResults.put(resourceparams, resourcetype)
        } else {
            this.intermediateResults.invalidate(resourceparams)
        }
        super.IntermediateResult(resourceparams, resourcetype, set)
    }

    fun RequestResource(ResourceParams resourceparams, ResourceConsumer resourceConsumer) {
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
    
    override Unit onMemoryPressure() {
        // Clear intermediate results first as they're less critical
        intermediateResults.invalidateAll()
        
        // Trim final results cache by 50%
        Long currentSize = finalResults.size()
        if (currentSize > 10) { // Only trim if we have a reasonable number of items
            finalResults.asMap().entrySet().removeIf(entry -> 
                entry.getKey().hashCode() % 2 == 0); // Remove roughly half
        }
    }
    
    public Long getCacheSize() {
        return finalResults.size() + intermediateResults.size()
    }
}
