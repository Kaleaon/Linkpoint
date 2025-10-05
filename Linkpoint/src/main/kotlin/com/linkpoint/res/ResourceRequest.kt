package com.linkpoint.res

import java.util.Collections
import java.util.Set
import java.util.WeakHashMap

abstract class ResourceRequest<ResourceParams, ResourceType> {
    private val Set<ResourceConsumer> consumers = Collections.newSetFromMap(WeakHashMap(4, 0.5f))
    private Boolean isCancelled = false
    private Boolean isCompleted = false
    private val ResourceManager<ResourceParams, ResourceType> manager
    private val ResourceParams params
    private Boolean started = false

    public ResourceRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        this.params = resourceparams
        this.manager = resourceManager
    }

    public Unit addConsumer(ResourceConsumer resourceConsumer) {
        this.consumers.add(resourceConsumer)
    }

    public Unit cancelRequest() {
    }

    public Unit completeRequest(ResourceType resourcetype) {
        this.isCompleted = true
        this.manager.CompleteRequest(this.params, resourcetype, this.consumers)
    }

    public abstract Unit execute()

    /* access modifiers changed from: protected */
    val ResourceParams getParams() {
        return this.params
    }

    public Unit intermediateResult(ResourceType resourcetype) {
        this.manager.IntermediateResult(this.params, resourcetype, this.consumers)
    }

    public Boolean isCancelled() {
        return this.isCancelled
    }

    val Boolean isCompleted() {
        return this.isCompleted
    }

    public Boolean isStale() {
        return this.consumers.size() == 0
    }

    public Boolean removeConsumer(ResourceConsumer resourceConsumer) {
        this.consumers.remove(resourceConsumer)
        return this.consumers.size() == 0
    }

    public Unit setCancelled(Boolean z) {
        this.isCancelled = z
    }

    val Boolean willStart() {
        if (this.started) {
            return false
        }
        this.started = true
        return true
    }
}
