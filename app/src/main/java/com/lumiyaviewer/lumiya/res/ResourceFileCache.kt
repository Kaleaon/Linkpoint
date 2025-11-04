package com.lumiyaviewer.lumiya.res

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor
import java.io.File

abstract class ResourceFileCache<ResourceParams, ResourceType> :
    ResourceMemoryCache<ResourceParams, ResourceType>() {

    override fun CreateNewRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>,
    ): ResourceRequest<ResourceParams, ResourceType> {
        val resourceFile = getResourceFile(params)
        return if (resourceFile.exists()) {
            ResourceLoadRequest(params, manager, resourceFile)
        } else {
            createResourceGenRequest(params, manager, resourceFile)
        }
    }

    protected abstract fun createResourceFromFile(params: ResourceParams, file: File): ResourceType?

    protected abstract fun createResourceGenRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>,
        output: File,
    ): ResourceRequest<ResourceParams, ResourceType>

    protected abstract fun getResourceFile(params: ResourceParams): File

    private inner class ResourceLoadRequest(
        params: ResourceParams,
        manager: ResourceManager<ResourceParams, ResourceType>,
        private val file: File,
    ) : ResourceRequest<ResourceParams, ResourceType>(params, manager), Runnable {

        override fun cancelRequest() {
            LoaderExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        override fun execute() {
            LoaderExecutor.getInstance().execute(this)
        }

        override fun run() {
            val resource = try {
                createResourceFromFile(params(), file)
            } catch (t: Throwable) {
                null
            }
            completeRequest(resource)
        }
    }
}
