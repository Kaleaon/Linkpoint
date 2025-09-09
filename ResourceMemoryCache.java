package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Set;

public abstract class ResourceMemoryCache<ResourceParams, ResourceType> extends ResourceManager<ResourceParams, ResourceType> {
    private final Cache<ResourceParams, ResourceType> finalResults = CacheBuilder.newBuilder().weakValues().build();
    private final Cache<ResourceParams, ResourceType> intermediateResults = CacheBuilder.newBuilder().weakValues().build();

    public void CompleteRequest(ResourceParams resourceParams, ResourceType resourceType, Set<ResourceConsumer> set) {
        if (resourceType != null) {
            this.finalResults.put(resourceParams, resourceType);
        } else {
            this.finalResults.invalidate(resourceParams);
        }
        super.CompleteRequest(resourceParams, resourceType, set);
    }

    public void IntermediateResult(ResourceParams resourceParams, ResourceType resourceType, Set<ResourceConsumer> set) {
        if (resourceType != null) {
            this.intermediateResults.put(resourceParams, resourceType);
        } else {
            this.intermediateResults.invalidate(resourceParams);
        }
        super.IntermediateResult(resourceParams, resourceType, set);
    }

    public void RequestResource(ResourceParams resourceParams, ResourceConsumer resourceConsumer) {
        Object ifPresent = this.finalResults.getIfPresent(resourceParams);
        if (ifPresent != null) {
            resourceConsumer.OnResourceReady(ifPresent, false);
            return;
        }
        ifPresent = this.intermediateResults.getIfPresent(resourceParams);
        if (ifPresent != null) {
            resourceConsumer.OnResourceReady(ifPresent, true);
        }
        super.RequestResource(resourceParams, resourceConsumer);
    }
}
