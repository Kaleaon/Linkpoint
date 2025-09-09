package com.lumiyaviewer.lumiya.res;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.lumiyaviewer.lumiya.res.executors.ResourceCleanupExecutor;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public abstract class ResourceManager<ResourceParams, ResourceType> {
    private final Queue<ResourceRequest<ResourceParams, ResourceType>> cancelledRequests = new ConcurrentLinkedQueue();
    private final Runnable cleanup = new Runnable() {
        public void run() {
            ResourceManager.this.collectReferences();
        }
    };
    private volatile ScheduledFuture<?> cleanupFuture = null;
    private final Cache<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> consumerMap = CacheBuilder.newBuilder().weakKeys().removalListener(this.removalListener).build();
    private final Object lock = new Object();
    private final RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalListener = new RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>>() {
        public void onRemoval(@Nonnull RemovalNotification<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalNotification) {
            ResourceConsumer resourceConsumer = (ResourceConsumer) removalNotification.getKey();
            ResourceRequest resourceRequest = (ResourceRequest) removalNotification.getValue();
            if (resourceRequest != null && !resourceRequest.isCompleted() && (resourceRequest.isCancelled() ^ 1) != 0) {
                if (resourceConsumer != null) {
                    resourceRequest.removeConsumer(resourceConsumer);
                }
                if (resourceRequest.isStale()) {
                    resourceRequest.setCancelled(true);
                    ResourceManager.this.cancelledRequests.add(resourceRequest);
                    if (((ResourceRequest) ResourceManager.this.requestMap.getIfPresent(resourceRequest.getParams())) == resourceRequest) {
                        ResourceManager.this.requestMap.invalidate(resourceRequest.getParams());
                    }
                }
            }
        }
    };
    private final LoadingCache<ResourceParams, ResourceRequest<ResourceParams, ResourceType>> requestMap = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>() {
        public ResourceRequest<ResourceParams, ResourceType> load(@Nonnull ResourceParams resourceParams) throws Exception {
            return ResourceManager.this.CreateNewRequest(resourceParams, ResourceManager.this);
        }
    });

    public void CancelRequest(ResourceConsumer resourceConsumer) {
        if (resourceConsumer != null) {
            synchronized (this.lock) {
                this.consumerMap.invalidate(resourceConsumer);
            }
            collectReferences();
        }
    }

    public void CompleteRequest(ResourceParams resourceParams, ResourceType resourceType, Set<ResourceConsumer> set) {
        Object<ResourceConsumer> arrayList;
        synchronized (this.lock) {
            this.requestMap.invalidate(resourceParams);
            arrayList = new ArrayList(set.size());
            for (ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    arrayList.add(resourceConsumer);
                    this.consumerMap.invalidate(resourceConsumer);
                }
            }
        }
        for (ResourceConsumer resourceConsumer2 : arrayList) {
            resourceConsumer2.OnResourceReady(resourceType, false);
        }
        arrayList.clear();
        collectReferences();
    }

    protected abstract ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceParams, ResourceManager<ResourceParams, ResourceType> resourceManager);

    public void IntermediateResult(ResourceParams resourceParams, ResourceType resourceType, Set<ResourceConsumer> set) {
        Object<ResourceConsumer> arrayList;
        synchronized (this.lock) {
            arrayList = new ArrayList(set.size());
            for (ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    arrayList.add(resourceConsumer);
                }
            }
        }
        for (ResourceConsumer resourceConsumer2 : arrayList) {
            resourceConsumer2.OnResourceReady(resourceType, true);
        }
        arrayList.clear();
        collectReferences();
    }

    public void RequestResource(ResourceParams resourceParams, ResourceConsumer resourceConsumer) {
        ResourceRequest resourceRequest;
        boolean willStart;
        synchronized (this.lock) {
            resourceRequest = (ResourceRequest) this.requestMap.getUnchecked(resourceParams);
            this.consumerMap.put(resourceConsumer, resourceRequest);
            resourceRequest.addConsumer(resourceConsumer);
            willStart = resourceRequest.willStart();
            if (this.cleanupFuture == null) {
                this.cleanupFuture = ResourceCleanupExecutor.getInstance().scheduleAtFixedRate(this.cleanup, 1, 1, TimeUnit.SECONDS);
            }
        }
        if (willStart) {
            resourceRequest.execute();
        }
    }

    protected void collectReferences() {
        ResourceRequest resourceRequest;
        Object obj = null;
        synchronized (this.lock) {
            this.requestMap.cleanUp();
            this.consumerMap.cleanUp();
            while (true) {
                resourceRequest = (ResourceRequest) this.cancelledRequests.poll();
                if (resourceRequest == null) {
                    break;
                }
                if (obj == null) {
                    obj = new ArrayList();
                }
                obj.add(resourceRequest);
            }
            if (this.requestMap.size() == 0 && this.consumerMap.size() == 0 && this.cancelledRequests.size() == 0) {
                if (this.cleanupFuture != null) {
                    this.cleanupFuture.cancel(false);
                }
                this.cleanupFuture = null;
            }
        }
        if (obj != null) {
            for (ResourceRequest resourceRequest2 : obj) {
                resourceRequest2.cancelRequest();
            }
            obj.clear();
        }
    }
}
