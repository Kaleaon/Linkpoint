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
/* loaded from: classes.dex */
public abstract class ResourceManager<ResourceParams, ResourceType> {
    private final Object lock = new Object();
    private volatile ScheduledFuture<?> cleanupFuture = null;
    private final Queue<ResourceRequest<ResourceParams, ResourceType>> cancelledRequests = new ConcurrentLinkedQueue();
    private final RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalListener = new RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>>() { // from class: com.lumiyaviewer.lumiya.res.ResourceManager.1
        @Override // com.google.common.cache.RemovalListener
        public void onRemoval(@Nonnull RemovalNotification<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalNotification) {
            ResourceConsumer key = removalNotification.getKey();
            ResourceRequest<ResourceParams, ResourceType> value = removalNotification.getValue();
            if (value == null || value.isCompleted() || !(!value.isCancelled())) {
                return;
            }
            if (key != null) {
                value.removeConsumer(key);
            }
            if (value.isStale()) {
                value.setCancelled(true);
                ResourceManager.this.cancelledRequests.add(value);
                if (((ResourceRequest) ResourceManager.this.requestMap.getIfPresent(value.getParams())) == value) {
                    ResourceManager.this.requestMap.invalidate(value.getParams());
                }
            }
        }
    };
    private final Cache<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> consumerMap = CacheBuilder.newBuilder().weakKeys().removalListener(this.removalListener).build();
    private final LoadingCache<ResourceParams, ResourceRequest<ResourceParams, ResourceType>> requestMap = (LoadingCache<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>) CacheBuilder.newBuilder().weakValues().build((CacheLoader<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>) new CacheLoader<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>() { // from class: com.lumiyaviewer.lumiya.res.ResourceManager.2
        @Override // com.google.common.cache.CacheLoader
        public ResourceRequest<ResourceParams, ResourceType> load(@Nonnull ResourceParams resourceparams) throws Exception {
            return ResourceManager.this.CreateNewRequest(resourceparams, ResourceManager.this);
        }

        @Override // com.google.common.cache.CacheLoader
        public /* bridge */ /* synthetic */ Object load(@Nonnull Object obj) throws Exception {
            return load((AnonymousClass2) obj);
        }
    });
    private final Runnable cleanup = new Runnable() { // from class: com.lumiyaviewer.lumiya.res.ResourceManager.3
        @Override // java.lang.Runnable
        public void run() {
            ResourceManager.this.collectReferences();
        }
    };

    public void CancelRequest(ResourceConsumer resourceConsumer) {
        if (resourceConsumer != null) {
            synchronized (this.lock) {
                this.consumerMap.invalidate(resourceConsumer);
            }
            collectReferences();
        }
    }

    public void CompleteRequest(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        ArrayList<ResourceConsumer> arrayList;
        synchronized (this.lock) {
            this.requestMap.invalidate(resourceparams);
            arrayList = new ArrayList(set.size());
            for (ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    arrayList.add(resourceConsumer);
                    this.consumerMap.invalidate(resourceConsumer);
                }
            }
        }
        for (ResourceConsumer resourceConsumer2 : arrayList) {
            resourceConsumer2.OnResourceReady(resourcetype, false);
        }
        arrayList.clear();
        collectReferences();
    }

    protected abstract ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager);

    public void IntermediateResult(ResourceParams resourceparams, ResourceType resourcetype, Set<ResourceConsumer> set) {
        ArrayList<ResourceConsumer> arrayList;
        synchronized (this.lock) {
            arrayList = new ArrayList(set.size());
            for (ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    arrayList.add(resourceConsumer);
                }
            }
        }
        for (ResourceConsumer resourceConsumer2 : arrayList) {
            resourceConsumer2.OnResourceReady(resourcetype, true);
        }
        arrayList.clear();
        collectReferences();
    }

    public void RequestResource(ResourceParams resourceparams, ResourceConsumer resourceConsumer) {
        ResourceRequest<ResourceParams, ResourceType> unchecked;
        boolean willStart;
        synchronized (this.lock) {
            unchecked = this.requestMap.getUnchecked(resourceparams);
            this.consumerMap.put(resourceConsumer, unchecked);
            unchecked.addConsumer(resourceConsumer);
            willStart = unchecked.willStart();
            if (this.cleanupFuture == null) {
                this.cleanupFuture = ResourceCleanupExecutor.getInstance().scheduleAtFixedRate(this.cleanup, 1L, 1L, TimeUnit.SECONDS);
            }
        }
        if (willStart) {
            unchecked.execute();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void collectReferences() {
        ArrayList<ResourceRequest> arrayList = null;
        synchronized (this.lock) {
            this.requestMap.cleanUp();
            this.consumerMap.cleanUp();
            while (true) {
                ResourceRequest<ResourceParams, ResourceType> poll = this.cancelledRequests.poll();
                if (poll == null) {
                    break;
                }
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(poll);
            }
            if (this.requestMap.size() == 0 && this.consumerMap.size() == 0 && this.cancelledRequests.size() == 0) {
                if (this.cleanupFuture != null) {
                    this.cleanupFuture.cancel(false);
                }
                this.cleanupFuture = null;
            }
        }
        if (arrayList != null) {
            for (ResourceRequest resourceRequest : arrayList) {
                resourceRequest.cancelRequest();
            }
            arrayList.clear();
        }
    }
}
