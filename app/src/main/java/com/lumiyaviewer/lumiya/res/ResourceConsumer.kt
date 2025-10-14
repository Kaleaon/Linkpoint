package com.lumiyaviewer.lumiya.res

/**
 * Modern Kotlin ResourceConsumer interface
 * Callback interface for resource loading completion
 */
interface ResourceConsumer {
    /**
     * Called when a resource is ready
     * @param resource The loaded resource
     * @param success Whether the resource loaded successfully
     */
    fun OnResourceReady(
        resource: Any?,
        success: Boolean,
    )
}
