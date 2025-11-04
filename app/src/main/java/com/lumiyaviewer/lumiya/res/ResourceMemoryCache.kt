package com.lumiyaviewer.lumiya.res

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

abstract class ResourceMemoryCache<ResourceParams, ResourceType> :
    ResourceManager<ResourceParams, ResourceType>() {

    private val finalResults: Cache<ResourceParams, ResourceType> =
        CacheBuilder.newBuilder().weakValues().build()

    private val intermediateResults: Cache<ResourceParams, ResourceType> =
        CacheBuilder.newBuilder().weakValues().build()

    override fun CompleteRequest(
        params: ResourceParams,
        resource: ResourceType?,
        consumers: Set<ResourceConsumer>,
    ) {
        if (resource != null) {
            finalResults.put(params, resource)
        } else {
            finalResults.invalidate(params)
        }
        super.CompleteRequest(params, resource, consumers)
    }

    override fun IntermediateResult(
        params: ResourceParams,
        resource: ResourceType?,
        consumers: Set<ResourceConsumer>,
    ) {
        if (resource != null) {
            intermediateResults.put(params, resource)
        } else {
            intermediateResults.invalidate(params)
        }
        super.IntermediateResult(params, resource, consumers)
    }

    override fun RequestResource(params: ResourceParams, consumer: ResourceConsumer) {
        finalResults.getIfPresent(params)?.let {
            consumer.OnResourceReady(it, false)
            return
        }

        intermediateResults.getIfPresent(params)?.let {
            consumer.OnResourceReady(it, true)
        }

        super.RequestResource(params, consumer)
    }

    fun clearCaches() {
        finalResults.invalidateAll()
        intermediateResults.invalidateAll()
    }
}
