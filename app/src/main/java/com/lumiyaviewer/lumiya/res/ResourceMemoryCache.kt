package com.lumiyaviewer.lumiya.res

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.memory.MemoryPressureListener

/**
 * Memory cache for resources with automatic memory pressure handling
 */
abstract class ResourceMemoryCache<ResourceParams, ResourceType>(
    private val memoryManager: MemoryManager
) : ResourceManager<ResourceParams, ResourceType>(), MemoryPressureListener {
    
    private val finalResults: Cache<ResourceParams, ResourceType>
    private val intermediateResults: Cache<ResourceParams, ResourceType>
    
    init {
        finalResults = CacheBuilder.newBuilder()
            .weakValues()
            .removalListener<ResourceParams, ResourceType> { notification ->
                notification.value?.let { value ->
                    val key = "final_${notification.key.hashCode()}"
                    memoryManager.trackDeallocation(key, estimateSize(value))
                }
            }
            .build()
        
        intermediateResults = CacheBuilder.newBuilder()
            .weakValues()
            .removalListener<ResourceParams, ResourceType> { notification ->
                notification.value?.let { value ->
                    val key = "intermediate_${notification.key.hashCode()}"
                    memoryManager.trackDeallocation(key, estimateSize(value))
                }
            }
            .build()
                
        memoryManager.addMemoryPressureListener(this)
    }
    
    /**
     * Estimate the memory size of a resource
     */
    protected abstract fun estimateSize(resource: ResourceType): Long

    /**
     * Complete a resource request with final result
     */
    fun CompleteRequest(params: ResourceParams, resource: ResourceType?, consumers: Set<ResourceConsumer>) {
        if (resource != null) {
            val key = "final_${params.hashCode()}"
            memoryManager.trackAllocation(key, resource, estimateSize(resource))
            finalResults.put(params, resource)
        } else {
            finalResults.invalidate(params)
        }
        super.CompleteRequest(params, resource, consumers)
    }

    /**
     * Provide intermediate result for a resource request
     */
    fun IntermediateResult(params: ResourceParams, resource: ResourceType?, consumers: Set<ResourceConsumer>) {
        if (resource != null) {
            val key = "intermediate_${params.hashCode()}"
            memoryManager.trackAllocation(key, resource, estimateSize(resource))
            intermediateResults.put(params, resource)
        } else {
            intermediateResults.invalidate(params)
        }
        super.IntermediateResult(params, resource, consumers)
    }

    /**
     * Request a resource, checking caches first
     */
    fun RequestResource(params: ResourceParams, consumer: ResourceConsumer) {
        // Check final results first
        finalResults.getIfPresent(params)?.let {
            consumer.OnResourceReady(it, isIntermediate = false)
            return
        }
        
        // Check intermediate results
        intermediateResults.getIfPresent(params)?.let {
            consumer.OnResourceReady(it, isIntermediate = true)
        }
        
        super.RequestResource(params, consumer)
    }
    
    /**
     * Handle memory pressure by clearing caches
     */
    override fun onMemoryPressure() {
        // Clear intermediate results first as they're less critical
        intermediateResults.invalidateAll()
        
        // Trim final results cache by approximately 50%
        val currentSize = finalResults.size()
        if (currentSize > 10) { // Only trim if we have a reasonable number of items
            finalResults.asMap().entries.removeIf { entry -> 
                entry.key.hashCode() % 2 == 0 // Remove roughly half
            }
        }
    }
    
    /**
     * Get total cache size
     */
    fun getCacheSize(): Long {
        return finalResults.size() + intermediateResults.size()
    }
}
