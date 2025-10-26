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
                        val key: String = "final_" + String.valueOf(notification.getKey().hashCode())
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()))
                    }
                })
                .build()
        
        this.intermediateResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        val key: String = "intermediate_" + String.valueOf(notification.getKey().hashCode())
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()))
                    }
                })
                .build()
                
        memoryManager.addMemoryPressureListener(this)
    }
    
    protected abstract Long estimateSize(ResourceType resource)

    fun CompleteRequest(resourceparams: ResourceParams, resourcetype: ResourceType, set: Set<ResourceConsumer>) {
        if (resourcetype != null) {
            val key: String = "final_" + String.valueOf(resourceparams.hashCode())
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype))
            this.finalResults.put(resourceparams, resourcetype)
        } else {
            this.finalResults.invalidate(resourceparams)
        }
        super.CompleteRequest(resourceparams, resourcetype, set)
    }

    fun IntermediateResult(resourceparams: ResourceParams, resourcetype: ResourceType, set: Set<ResourceConsumer>) {
        if (resourcetype != null) {
            val key: String = "intermediate_" + String.valueOf(resourceparams.hashCode())
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype))
            this.intermediateResults.put(resourceparams, resourcetype)
        } else {
            this.intermediateResults.invalidate(resourceparams)
        }
        super.IntermediateResult(resourceparams, resourcetype, set)
    }

    fun RequestResource(resourceparams: ResourceParams, resourceConsumer: ResourceConsumer) {
        val ifPresent: ResourceType = this.finalResults.getIfPresent(resourceparams)
        if (ifPresent != null) {
            resourceConsumer.OnResourceReady(ifPresent, false)
            return
        }
        val ifPresent2: ResourceType = this.intermediateResults.getIfPresent(resourceparams)
        if (ifPresent2 != null) {
            resourceConsumer.OnResourceReady(ifPresent2, true)
        }
        super.RequestResource(resourceparams, resourceConsumer)
    }
    
    override Unit onMemoryPressure() {
        // Clear intermediate results first as they're less critical
        intermediateResults.invalidateAll()
        
        // Trim final results cache by 50%
        val currentSize: Long = finalResults.size()
        if (currentSize > 10) { // Only trim if we have a reasonable number of items
            finalResults.asMap().entrySet().removeIf(entry -> 
                entry.getKey().hashCode() % 2 == 0); // Remove roughly half
        }
    }
    
     public fun getCacheSize(): Long {
        return finalResults.size() + intermediateResults.size()
    }
}
