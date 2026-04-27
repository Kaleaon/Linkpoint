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
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class ResourceManager<ResourceParams, ResourceType> {

    private val lock = Any()
    private val cancelledRequests: Queue<ResourceRequest<ResourceParams, ResourceType>> =
        ConcurrentLinkedQueue()

    private val removalListener = RemovalListener<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> { notification: RemovalNotification<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> ->
        val consumer = notification.key
        val request = notification.value
        if (request != null && !request.isCompleted() && !request.isCancelled()) {
            consumer?.let { request.removeConsumer(it) }
            if (request.isStale()) {
                request.setCancelled(true)
                cancelledRequests.add(request)
                if (requestMap.getIfPresent(request.params()) === request) {
                    requestMap.invalidate(request.params())
                }
            }
        }
    }

    private val consumerMap: Cache<ResourceConsumer, ResourceRequest<ResourceParams, ResourceType>> =
        CacheBuilder.newBuilder()
            .weakKeys()
            .removalListener(removalListener)
            .build()

    private val requestMap: LoadingCache<ResourceParams, ResourceRequest<ResourceParams, ResourceType>> =
        CacheBuilder.newBuilder()
            .weakValues()
            .build(object : CacheLoader<ResourceParams, ResourceRequest<ResourceParams, ResourceType>>() {
                override fun load(key: ResourceParams): ResourceRequest<ResourceParams, ResourceType> {
                    return CreateNewRequest(key, this@ResourceManager)
                }
            })

    private val cleanupTask = Runnable { collectReferences() }

    @Volatile
    private var cleanupFuture: ScheduledFuture<*>? = null

    fun CancelRequest(consumer: ResourceConsumer?) {
        if (consumer == null) return
        synchronized(lock) {
            consumerMap.invalidate(consumer)
        }
        collectReferences()
    }

    fun CompleteRequest(
        params: ResourceParams,
        resource: ResourceType?,
        consumers: Set<ResourceConsumer>,
    ) {
        val targets: List<ResourceConsumer>
        synchronized(lock) {
            requestMap.invalidate(params)
            targets = consumers.filterNotNullTo(ArrayList(consumers.size))
            targets.forEach { consumerMap.invalidate(it) }
        }
        targets.forEach { it.OnResourceReady(resource, false) }
        collectReferences()
    }

    fun IntermediateResult(
        params: ResourceParams,
        resource: ResourceType?,
        consumers: Set<ResourceConsumer>,
    ) {
        val targets: List<ResourceConsumer>
        synchronized(lock) {
            targets = consumers.filterNotNullTo(ArrayList(consumers.size))
        }
        targets.forEach { it.OnResourceReady(resource, true) }
        collectReferences()
    }

    fun RequestResource(params: ResourceParams, consumer: ResourceConsumer) {
        val request: ResourceRequest<ResourceParams, ResourceType>
        val shouldStart: Boolean
        synchronized(lock) {
            request = requestMap.getUnchecked(params)
            consumerMap.put(consumer, request)
            request.addConsumer(consumer)
            shouldStart = request.willStart()
            if (cleanupFuture == null) {
                cleanupFuture = ResourceCleanupExecutor.getInstance()
                    .scheduleAtFixedRate(cleanupTask, 1, 1, TimeUnit.SECONDS)
            }
        }
        if (shouldStart) {
            request.execute()
        }
    }

    protected fun collectReferences() {
        var stale: MutableList<ResourceRequest<ResourceParams, ResourceType>>? = null
        synchronized(lock) {
            requestMap.cleanUp()
            consumerMap.cleanUp()
            while (true) {
                val request = cancelledRequests.poll() ?: break
                if (stale == null) stale = ArrayList()
                stale!!.add(request)
            }

            if (requestMap.size() == 0L && consumerMap.size() == 0L && cancelledRequests.isEmpty()) {
                cleanupFuture?.cancel(false)
                cleanupFuture = null
            }
        }

        stale?.forEach { it.cancelRequest() }
        stale?.clear()
    }

    protected abstract fun CreateNewRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>,
    ): ResourceRequest<ResourceParams, ResourceType>
}
