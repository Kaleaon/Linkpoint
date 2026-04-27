// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res;

import java.util.Set;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Cache;

public abstract class ResourceMemoryCache<ResourceParams, ResourceType> extends ResourceManager<ResourceParams, ResourceType>
{
    private final Cache<ResourceParams, ResourceType> finalResults;
    private final Cache<ResourceParams, ResourceType> intermediateResults;
    
    public ResourceMemoryCache() {
        this.finalResults = CacheBuilder.newBuilder().weakValues().build();
        this.intermediateResults = CacheBuilder.newBuilder().weakValues().build();
    }
    
    @Override
    public void CompleteRequest(final ResourceParams resourceParams, final ResourceType resourceType, final Set<ResourceConsumer> set) {
        if (resourceType != null) {
            this.finalResults.put(resourceParams, resourceType);
        }
        else {
            this.finalResults.invalidate(resourceParams);
        }
        super.CompleteRequest(resourceParams, resourceType, set);
    }
    
    @Override
    public void IntermediateResult(final ResourceParams resourceParams, final ResourceType resourceType, final Set<ResourceConsumer> set) {
        if (resourceType != null) {
            this.intermediateResults.put(resourceParams, resourceType);
        }
        else {
            this.intermediateResults.invalidate(resourceParams);
        }
        super.IntermediateResult(resourceParams, resourceType, set);
    }
    
    @Override
    public void RequestResource(final ResourceParams resourceParams, final ResourceConsumer resourceConsumer) {
        final ResourceType ifPresent = this.finalResults.getIfPresent(resourceParams);
        if (ifPresent != null) {
            resourceConsumer.OnResourceReady(ifPresent, false);
            return;
        }
        final ResourceType ifPresent2 = this.intermediateResults.getIfPresent(resourceParams);
        if (ifPresent2 != null) {
            resourceConsumer.OnResourceReady(ifPresent2, true);
        }
        super.RequestResource(resourceParams, resourceConsumer);
    }
}
