package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest

/**
 * Base class for caching GL resources
 * Manages loading raw resources onto the GPU
 */
abstract class GLResourceCache<ResourceParams, RawType, ResourceType : GLSizedResource>(
    memoryManager: MemoryManager,
    private val loadQueue: GLLoadQueue
) : ResourceMemoryCache<ResourceParams, ResourceType>(memoryManager) {

    /**
     * Load request that handles GPU resource loading
     */
    private inner class LoadRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>
    ) : ResourceRequest<ResourceParams, ResourceType>(params, manager), 
        GLLoadQueue.GLLoadable, 
        ResourceConsumer {
        
        @Volatile private var finalResult = false
        @Volatile private var loadedFinal = false
        @Volatile private var loadedResource: ResourceType? = null
        @Volatile private var rawResource: RawType? = null

        /**
         * Complete the GL load on render thread
         */
        override fun GLCompleteLoad() {
            val (resource, isFinal) = synchronized(this) {
                Pair(loadedResource, loadedFinal)
            }
            
            if (isFinal) {
                completeRequest(resource)
            } else {
                intermediateResult(resource)
            }
        }

        /**
         * Get the size of the resource to load
         */
        override fun GLGetLoadSize(): Int {
            val raw = synchronized(this) { rawResource }
            return raw?.let { GetResourceSize(it) } ?: 0
        }

        /**
         * Load the resource on the GL thread
         */
        override fun GLLoad(
            renderContext: RenderContext,
            loadHandler: GLLoadQueue.GLLoadHandler
        ): Int {
            val (raw, isFinal) = synchronized(this) {
                Pair(rawResource, finalResult)
            }
            
            val loadedRes = LoadResource(getParams(), raw, renderContext)
            val loadedSize = loadedRes?.getLoadedSize() ?: 0
            
            synchronized(this) {
                loadedResource = loadedRes
                loadedFinal = isFinal
            }
            
            loadedRes?.let {
                loadHandler.GLResourceLoaded(this)
            }
            
            return loadedSize
        }

        /**
         * Called when raw resource is ready
         */
        override fun OnResourceReady(resource: Any?, isIntermediate: Boolean) {
            if (resource != null) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val rawRes = resource as RawType
                    
                    synchronized(this) {
                        rawResource = rawRes
                        finalResult = !isIntermediate
                    }
                    loadQueue.add(this)
                } catch (e: ClassCastException) {
                    Debug.Warning(e)
                    completeRequest(null)
                }
            } else {
                completeRequest(null)
            }
            collectReferences()
        }

        /**
         * Cancel the load request
         */
        override fun cancelRequest() {
            loadQueue.remove(this)
            CancelRawResource(this)
            super.cancelRequest()
        }

        /**
         * Execute the request
         */
        override fun execute() {
            RequestRawResource(getParams(), this)
        }
    }

    /**
     * Create a new load request
     */
    override fun CreateNewRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>
    ): ResourceRequest<ResourceParams, ResourceType> {
        return LoadRequest(params, manager)
    }

    /**
     * Cancel loading of raw resource
     */
    protected abstract fun CancelRawResource(consumer: ResourceConsumer)

    /**
     * Get the size of a raw resource
     */
    protected abstract fun GetResourceSize(raw: RawType?): Int

    /**
     * Load raw resource into GL resource
     */
    protected abstract fun LoadResource(
        params: ResourceParams,
        raw: RawType?,
        renderContext: RenderContext
    ): ResourceType?

    /**
     * Request loading of raw resource
     */
    protected abstract fun RequestRawResource(
        params: ResourceParams,
        consumer: ResourceConsumer
    )
}
