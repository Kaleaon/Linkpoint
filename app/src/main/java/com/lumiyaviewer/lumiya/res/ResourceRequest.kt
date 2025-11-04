package com.lumiyaviewer.lumiya.res

import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Base class for asynchronous resource population requests.
 * Consumers are tracked via a weak set so that UI components can simply
 * drop their strong reference when they are destroyed.
 */
abstract class ResourceRequest<ResourceParams, ResourceType>(
    private val params: ResourceParams,
    private val manager: ResourceManager<ResourceParams, ResourceType>,
) {

    private val consumers: MutableSet<ResourceConsumer> =
        Collections.newSetFromMap(WeakHashMap<ResourceConsumer, Boolean>(4, 0.5f))

    private val cancelled = AtomicBoolean(false)
    private val completed = AtomicBoolean(false)
    private val started = AtomicBoolean(false)

    fun addConsumer(consumer: ResourceConsumer) {
        consumers.add(consumer)
    }

    open fun cancelRequest() {
        // override in subclasses when additional cleanup is needed
    }

    protected fun completeRequest(resource: ResourceType?) {
        completed.set(true)
        manager.completeRequest(params, resource, consumers)
    }

    protected fun intermediateResult(resource: ResourceType?) {
        manager.intermediateResult(params, resource, consumers)
    }

    fun removeConsumer(consumer: ResourceConsumer): Boolean {
        consumers.remove(consumer)
        return consumers.isEmpty()
    }

    fun isStale(): Boolean = consumers.isEmpty()

    fun isCancelled(): Boolean = cancelled.get()

    fun setCancelled(value: Boolean) {
        cancelled.set(value)
    }

    fun isCompleted(): Boolean = completed.get()

    internal fun params(): ResourceParams = params

    /**
     * Ensures the request is only executed once even if multiple consumers
     * subscribe before the first result is produced.
     *
     * @return true when the caller should trigger [execute].
     */
    fun willStart(): Boolean = started.compareAndSet(false, true)

    abstract fun execute()
}
