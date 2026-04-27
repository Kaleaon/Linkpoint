// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.lumiyaviewer.lumiya.res.executors.ResourceCleanupExecutor;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import javax.annotation.Nonnull;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.Cache;
import java.util.concurrent.ScheduledFuture;
import java.util.Queue;

public abstract class ResourceManager<ResourceParams, ResourceType>
{
    private final Queue<ResourceRequest<ResourceParams, ResourceType>> cancelledRequests;
    private final Runnable cleanup;
    private volatile ScheduledFuture<?> cleanupFuture;
    private final Cache<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> consumerMap;
    private final Object lock;
    private final RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalListener;
    private final LoadingCache<ResourceParams, ResourceRequest<ResourceParams, ResourceType>> requestMap;
    
    public ResourceManager() {
        this.lock = new Object();
        this.cleanupFuture = null;
        this.cancelledRequests = new ConcurrentLinkedQueue<ResourceRequest<ResourceParams, ResourceType>>();
        this.removalListener = new RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>>() {
            @Override
            public void onRemoval(@Nonnull final RemovalNotification<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalNotification) {
                final ResourceConsumer resourceConsumer = removalNotification.getKey();
                final ResourceRequest resourceRequest = removalNotification.getValue();
                if (resourceRequest != null && !resourceRequest.isCompleted() && (resourceRequest.isCancelled() ^ true)) {
                    if (resourceConsumer != null) {
                        resourceRequest.removeConsumer(resourceConsumer);
                    }
                    if (resourceRequest.isStale()) {
                        resourceRequest.setCancelled(true);
                        ResourceManager.this.cancelledRequests.add(resourceRequest);
                        if (ResourceManager.this.requestMap.getIfPresent(resourceRequest.getParams()) == resourceRequest) {
                            ResourceManager.this.requestMap.invalidate(resourceRequest.getParams());
                        }
                    }
                }
            }
        };
        this.consumerMap = CacheBuilder.newBuilder().weakKeys().removalListener((RemovalListener<? super Object, ? super Object>)this.removalListener).build();
        this.requestMap = CacheBuilder.newBuilder().weakValues().build((CacheLoader<? super ResourceParams, ResourceRequest<ResourceParams, ResourceType>>)new CacheLoader<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>() {
            @Override
            public ResourceRequest<ResourceParams, ResourceType> load(@Nonnull final ResourceParams resourceParams) throws Exception {
                return ResourceManager.this.CreateNewRequest(resourceParams, ResourceManager.this);
            }
        });
        this.cleanup = new Runnable() {
            @Override
            public void run() {
                ResourceManager.this.collectReferences();
            }
        };
    }
    
    public void CancelRequest(final ResourceConsumer resourceConsumer) {
        if (resourceConsumer == null) {
            return;
        }
        synchronized (this.lock) {
            this.consumerMap.invalidate(resourceConsumer);
            monitorexit(this.lock);
            this.collectReferences();
        }
    }
    
    public void CompleteRequest(final ResourceParams resourceParams, final ResourceType resourceType, final Set<ResourceConsumer> set) {
        synchronized (this.lock) {
            this.requestMap.invalidate(resourceParams);
            final ArrayList list = new ArrayList(set.size());
            for (final ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    list.add(resourceConsumer);
                    this.consumerMap.invalidate(resourceConsumer);
                }
            }
        }
        final Object o;
        monitorexit(o);
        final Iterable iterable;
        final Iterator iterator2 = iterable.iterator();
        while (iterator2.hasNext()) {
            ((ResourceConsumer)iterator2.next()).OnResourceReady(resourceType, false);
        }
        ((List)iterable).clear();
        this.collectReferences();
    }
    
    protected abstract ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(final ResourceParams p0, final ResourceManager<ResourceParams, ResourceType> p1);
    
    public void IntermediateResult(final ResourceParams resourceParams, final ResourceType resourceType, final Set<ResourceConsumer> set) {
        synchronized (this.lock) {
            final ArrayList list = new ArrayList(set.size());
            for (final ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    list.add(resourceConsumer);
                }
            }
        }
        final Object o;
        monitorexit(o);
        final Iterable iterable;
        final Iterator iterator2 = iterable.iterator();
        while (iterator2.hasNext()) {
            ((ResourceConsumer)iterator2.next()).OnResourceReady(resourceType, true);
        }
        ((List)iterable).clear();
        this.collectReferences();
    }
    
    public void RequestResource(final ResourceParams resourceParams, final ResourceConsumer resourceConsumer) {
        synchronized (this.lock) {
            final ResourceRequest resourceRequest = this.requestMap.getUnchecked(resourceParams);
            this.consumerMap.put(resourceConsumer, resourceRequest);
            resourceRequest.addConsumer(resourceConsumer);
            final boolean willStart = resourceRequest.willStart();
            if (this.cleanupFuture == null) {
                this.cleanupFuture = ResourceCleanupExecutor.getInstance().scheduleAtFixedRate(this.cleanup, 1L, 1L, TimeUnit.SECONDS);
            }
            monitorexit(this.lock);
            if (willStart) {
                resourceRequest.execute();
            }
        }
    }
    
    protected void collectReferences() {
        List<ResourceRequest> list = null;
        synchronized (this.lock) {
            this.requestMap.cleanUp();
            this.consumerMap.cleanUp();
            while (true) {
                final ResourceRequest resourceRequest = this.cancelledRequests.poll();
                if (resourceRequest == null) {
                    break;
                }
                List<ResourceRequest> list2;
                if ((list2 = list) == null) {
                    list2 = new ArrayList<ResourceRequest>();
                }
                list2.add(resourceRequest);
                list = list2;
            }
        }
        if (this.requestMap.size() == 0L && this.consumerMap.size() == 0L && this.cancelledRequests.size() == 0) {
            if (this.cleanupFuture != null) {
                this.cleanupFuture.cancel(false);
            }
            this.cleanupFuture = null;
        }
        final Object o;
        monitorexit(o);
        final Iterable iterable;
        if (iterable != null) {
            final Iterator iterator = iterable.iterator();
            while (iterator.hasNext()) {
                ((ResourceRequest)iterator.next()).cancelRequest();
            }
            ((List)iterable).clear();
        }
    }
}
