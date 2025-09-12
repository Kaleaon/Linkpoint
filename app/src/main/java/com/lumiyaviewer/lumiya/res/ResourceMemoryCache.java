package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lumiyaviewer.lumiya.memory.MemoryManager;
import com.lumiyaviewer.lumiya.memory.MemoryPressureListener;
import java.util.Set;

public abstract class ResourceMemoryCache<ResourceParams, ResourceType> extends ResourceManager<ResourceParams, ResourceType> implements MemoryPressureListener {
    private final Cache<ResourceParams, ResourceType> finalResults;
    private final Cache<ResourceParams, ResourceType> intermediateResults;
    private final MemoryManager memoryManager;
    
    public ResourceMemoryCache(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        this.finalResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        String key = "final_" + String.valueOf(notification.getKey().hashCode());
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()));
                    }
                })
                .build();
        
        this.intermediateResults = CacheBuilder.newBuilder()
                .weakValues()
                .removalListener(notification -> {
                    if (notification.getValue() != null) {
                        String key = "intermediate_" + String.valueOf(notification.getKey().hashCode());
                        this.memoryManager.trackDeallocation(key, estimateSize(notification.getValue()));
                    }
                })
                .build();
                
        memoryManager.addMemoryPressureListener(this);
    }
    
    protected abstract long estimateSize(ResourceType resource);

    public void CompleteRequest(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        if (resourcetype != null) {
            String key = "final_" + String.valueOf(resourceparams.hashCode());
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype));
            this.finalResults.put(resourceparams, resourcetype);
        } else {
            this.finalResults.invalidate(resourceparams);
        }
        super.CompleteRequest(resourceparams, resourcetype, set);
    }

    public void IntermediateResult(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        if (resourcetype != null) {
            String key = "intermediate_" + String.valueOf(resourceparams.hashCode());
            memoryManager.trackAllocation(key, resourcetype, estimateSize(resourcetype));
            this.intermediateResults.put(resourceparams, resourcetype);
        } else {
            this.intermediateResults.invalidate(resourceparams);
        }
        super.IntermediateResult(resourceparams, resourcetype, set);
    }

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
    
    @Override
    public void onMemoryPressure() {
        // Clear intermediate results first as they're less critical
        intermediateResults.invalidateAll();
        
        // Trim final results cache by 50%
        long currentSize = finalResults.size();
        if (currentSize > 10) { // Only trim if we have a reasonable number of items
            finalResults.asMap().entrySet().removeIf(entry -> 
                entry.getKey().hashCode() % 2 == 0); // Remove roughly half
        }
    }
    
    public long getCacheSize() {
        return finalResults.size() + intermediateResults.size();
    }
}
