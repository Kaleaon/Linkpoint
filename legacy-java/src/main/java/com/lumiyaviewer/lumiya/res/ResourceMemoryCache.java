package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Set;

/* loaded from: classes.dex */
public abstract class ResourceMemoryCache<ResourceParams, ResourceType> extends ResourceManager<ResourceParams, ResourceType> {
    private final Cache<ResourceParams, ResourceType> finalResults = (Cache<ResourceParams, ResourceType>) CacheBuilder.newBuilder().weakValues().build();
    private final Cache<ResourceParams, ResourceType> intermediateResults = (Cache<ResourceParams, ResourceType>) CacheBuilder.newBuilder().weakValues().build();

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    public void CompleteRequest(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        if (resourcetype != null) {
            this.finalResults.put(resourceparams, resourcetype);
        } else {
            this.finalResults.invalidate(resourceparams);
        }
        super.CompleteRequest(resourceparams, resourcetype, set);
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    public void IntermediateResult(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        if (resourcetype != null) {
            this.intermediateResults.put(resourceparams, resourcetype);
        } else {
            this.intermediateResults.invalidate(resourceparams);
        }
        super.IntermediateResult(resourceparams, resourcetype, set);
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    public void RequestResource(ResourceParams resourceparams, ResourceConsumer resourceConsumer) {
        ResourceType ifPresent = this.finalResults.getIfPresent(resourceparams);
        if (ifPresent != null) {
            resourceConsumer.OnResourceReady(ifPresent, false);
            return;
        }
        ResourceType ifPresent2 = this.intermediateResults.getIfPresent(resourceparams);
        if (ifPresent2 != null) {
            resourceConsumer.OnResourceReady(ifPresent2, true);
        }
        super.RequestResource(resourceparams, resourceConsumer);
    }
}
