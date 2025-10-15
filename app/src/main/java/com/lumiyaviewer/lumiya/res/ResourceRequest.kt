package com.lumiyaviewer.lumiya.res

import java.util.Collections
import java.util.Set
import java.util.WeakHashMap

abstract class ResourceRequest<ResourceParams, ResourceType> {
    private val consumers: Set<ResourceConsumer> = Collections.newSetFromMap(WeakHashMap(4, 0.5f))
    private var isCancelled: Boolean = false
    private var isCompleted: Boolean = false
    private ResourceManager<ResourceParams, ResourceType> manager
    private ResourceParams params
    private var started: Boolean = false

    constructor(resourceparams: ResourceParams, resourceManager: ResourceType>) {
        this.params = resourceparams
        this.manager = resourceManager
    }

    fun addConsumer(resourceConsumer: ResourceConsumer): Unit {
        this.consumers.add(resourceConsumer)
    }

    fun cancelRequest(): Unit {
    }

    fun completeRequest(resourcetype: ResourceType): Unit {
        this.isCompleted = true
        this.manager.CompleteRequest(this.params, resourcetype, this.consumers)
    }

    abstract fun execute(): Unit

    /* access modifiers changed from: protected */
    ResourceParams getParams() {
        return this.params
    }

    fun intermediateResult(resourcetype: ResourceType): Unit {
        this.manager.IntermediateResult(this.params, resourcetype, this.consumers)
    }

    fun isCancelled(): Boolean {
        return this.isCancelled
    }

    boolean isCompleted() {
        return this.isCompleted
    }

    fun isStale(): Boolean {
        return this.consumers.size() == 0
    }

    fun removeConsumer(resourceConsumer: ResourceConsumer): Boolean {
        this.consumers.remove(resourceConsumer)
        return this.consumers.size() == 0
    }

    fun setCancelled(z: Boolean): Unit {
        this.isCancelled = z
    }

    boolean willStart() {
        if (this.started) {
            return false
        }
        this.started = true
        return true
    }
}
