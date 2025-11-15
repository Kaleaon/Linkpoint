package com.lumiyaviewer.lumiya.res

/**
 * Callback invoked when a resource request has produced either an
 * intermediate or final result.
 */
fun interface ResourceConsumer {
    /**
     * @param resource       the resource instance (or null on failure)
     * @param isIntermediate true when delivery represents a streaming/intermediate result
     */
    fun OnResourceReady(resource: Any?, isIntermediate: Boolean)
}
