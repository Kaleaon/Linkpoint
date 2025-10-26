package com.lumiyaviewer.lumiya.res

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalListener
import com.google.common.cache.RemovalNotification
import com.lumiyaviewer.lumiya.res.executors.ResourceCleanupExecutor
import java.util.ArrayList
import java.util.Queue
import java.util.Set
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import androidx.annotation.NonNull

abstract class ResourceManager<ResourceParams, ResourceType> {
    /* access modifiers changed from: private */
    Queue<ResourceRequest<ResourceParams, ResourceType>> cancelledRequests = ConcurrentLinkedQueue()
    private val cleanup: Runnable = Runnable() {
        fun run(): Unit {
            ResourceManager.this.collectReferences()
        }
    }
    private volatile ScheduledFuture<?> cleanupFuture = null
    private Cache<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> consumerMap = CacheBuilder.newBuilder().weakKeys().removalListener(this.removalListener).build()
    private val lock: Any = Object()
    private RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> removalListener = new RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>>() {
        fun onRemoval(removalNotification: ResourceType>>): Unit {
            ResourceConsumer key = removalNotification.getKey()
            ResourceRequest value = removalNotification.getValue()
            if (value != null && !value.isCompleted() && (!value.isCancelled())) {
                if (key != null) {
                    value.removeConsumer(key)
                }
                if (value.isStale()) {
                    value.setCancelled(true)
                    ResourceManager.this.cancelledRequests.add(value)
                    if (((ResourceRequest) ResourceManager.this.requestMap.getIfPresent(value.getParams())) == value) {
                        ResourceManager.this.requestMap.invalidate(value.getParams())
                    }
                }
            }
        }
    }
    /* access modifiers changed from: private */
    LoadingCache<ResourceParams, ResourceRequest<ResourceParams, ResourceType>> requestMap = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>() {
        ResourceRequest<ResourceParams, ResourceType> load(@NonNull ResourceParams resourceparams) throws Exception {
            return ResourceManager.this.CreateNewRequest(resourceparams, ResourceManager.this)
        }

    fun CancelRequest(resourceConsumer: ResourceConsumer): Unit {
        if (resourceConsumer != null) {
            synchronized (this.lock) {
                this.consumerMap.invalidate(resourceConsumer)
            }
            collectReferences()
        }
    }

    fun CompleteRequest(resourceparams: ResourceParams, resourcetype: ResourceType, set: Set<ResourceConsumer>): Unit {
        ArrayList<ResourceConsumer> arrayList
        synchronized (this.lock) {
            this.requestMap.invalidate(resourceparams)
            arrayList = new ArrayList<>(set.size())
            for (ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    arrayList.add(resourceConsumer)
                    this.consumerMap.invalidate(resourceConsumer)
                }
            }
        }
        for (ResourceConsumer OnResourceReady : arrayList) {
            OnResourceReady.OnResourceReady(resourcetype, false)
        }
        arrayList.clear()
        collectReferences()
    }

    /* access modifiers changed from: protected */
    abstract fun CreateNewRequest(resourceparams: ResourceParams, resourceManager: ResourceType>): ResourceRequest<ResourceParams, ResourceType>

    fun IntermediateResult(resourceparams: ResourceParams, resourcetype: ResourceType, set: Set<ResourceConsumer>): Unit {
        ArrayList<ResourceConsumer> arrayList
        synchronized (this.lock) {
            arrayList = new ArrayList<>(set.size())
            for (ResourceConsumer resourceConsumer : set) {
                if (resourceConsumer != null) {
                    arrayList.add(resourceConsumer)
                }
            }
        }
        for (ResourceConsumer OnResourceReady : arrayList) {
            OnResourceReady.OnResourceReady(resourcetype, true)
        }
        arrayList.clear()
        collectReferences()
    }

    fun RequestResource(resourceparams: ResourceParams, resourceConsumer: ResourceConsumer): Unit {
        ResourceRequest unchecked
        boolean willStart
        synchronized (this.lock) {
            unchecked = this.requestMap.getUnchecked(resourceparams)
            this.consumerMap.put(resourceConsumer, unchecked)
            unchecked.addConsumer(resourceConsumer)
            willStart = unchecked.willStart()
            if (this.cleanupFuture == null) {
                this.cleanupFuture = ResourceCleanupExecutor.getInstance().scheduleAtFixedRate(this.cleanup, 1, 1, TimeUnit.SECONDS)
            }
        }
        if (willStart) {
            unchecked.execute()
        }
    }

    /* access modifiers changed from: protected */
    fun collectReferences(): Unit {
        ArrayList<ResourceRequest> arrayList = null
        synchronized (this.lock) {
            this.requestMap.cleanUp()
            this.consumerMap.cleanUp()
            while (true) {
                ResourceRequest poll = this.cancelledRequests.poll()
                if (poll == null) {
                    break
                }
                if (arrayList == null) {
                    arrayList = new ArrayList<>()
                }
                arrayList.add(poll)
            }
            if (this.requestMap.size() == 0 && this.consumerMap.size() == 0 && this.cancelledRequests.size() == 0) {
                if (this.cleanupFuture != null) {
                    this.cleanupFuture.cancel(false)
                }
                this.cleanupFuture = null
            }
        }
        if (arrayList != null) {
            for (ResourceRequest cancelRequest : arrayList) {
                cancelRequest.cancelRequest()
            }
            arrayList.clear()
        }
    }
}
