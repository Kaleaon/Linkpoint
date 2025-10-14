package com.lumiyaviewer.lumiya.res

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor
import java.io.File

abstract class ResourceFileCache<ResourceParams, ResourceType> extends ResourceMemoryCache<ResourceParams, ResourceType> {

    private class ResourceLoadRequest<ResParams extends ResourceParams, ResType extends ResourceType> extends ResourceRequest<ResourceParams, ResourceType> implements Runnable {
        private File file

        ResourceLoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file2) {
            super(resourceparams, resourceManager)
            this.file = file2
        }

        fun cancelRequest(): Unit {
            LoaderExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            LoaderExecutor.getInstance().execute(this)
        }

        fun run(): Unit {
            try {
                completeRequest(ResourceFileCache.this.createResourceFromFile(getParams(), this.file))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    /* access modifiers changed from: protected */
    fun CreateNewRequest(resourceparams: ResourceParams, resourceManager: ResourceType>): ResourceRequest<ResourceParams, ResourceType> {
        File resourceFile = getResourceFile(resourceparams)
        return resourceFile.exists() ? new ResourceLoadRequest(resourceparams, resourceManager, resourceFile) : createResourceGenRequest(resourceparams, resourceManager, resourceFile)
    }

    /* access modifiers changed from: protected */
    abstract fun createResourceFromFile(resourceparams: ResourceParams, file: File): ResourceType

    /* access modifiers changed from: protected */
    abstract fun createResourceGenRequest(resourceparams: ResourceParams, resourceManager: ResourceType>, file: File): ResourceRequest<ResourceParams, ResourceType>

    /* access modifiers changed from: protected */
    abstract fun getResourceFile(resourceparams: ResourceParams): File
}
