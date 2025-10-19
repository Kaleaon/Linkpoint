package com.lumiyaviewer.lumiya.res

import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor
import java.io.File

/**
 * File-backed resource cache that extends memory cache
 */
abstract class ResourceFileCache<ResourceParams, ResourceType>(
    memoryManager: MemoryManager
) : ResourceMemoryCache<ResourceParams, ResourceType>(memoryManager) {

    /**
     * Resource load request from file
     */
    private inner class ResourceLoadRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>,
        private val file: File
    ) : ResourceRequest<ResourceParams, ResourceType>(params, manager), Runnable {

        override fun cancelRequest() {
            LoaderExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        override fun execute() {
            LoaderExecutor.getInstance().execute(this)
        }

        override fun run() {
            try {
                completeRequest(createResourceFromFile(getParams(), file))
            } catch (e: Exception) {
                completeRequest(null)
            }
        }
    }

    /**
     * Create a new resource request
     */
    protected fun CreateNewRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>
    ): ResourceRequest<ResourceParams, ResourceType> {
        val resourceFile = getResourceFile(params)
        return if (resourceFile.exists()) {
            ResourceLoadRequest(params, manager, resourceFile)
        } else {
            createResourceGenRequest(params, manager, resourceFile)
        }
    }

    /**
     * Create resource from file
     */
    protected abstract fun createResourceFromFile(params: ResourceParams, file: File): ResourceType

    /**
     * Create resource generation request for missing files
     */
    protected abstract fun createResourceGenRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>,
        file: File
    ): ResourceRequest<ResourceParams, ResourceType>

    /**
     * Get file path for resource parameters
     */
    protected abstract fun getResourceFile(params: ResourceParams): File
}
