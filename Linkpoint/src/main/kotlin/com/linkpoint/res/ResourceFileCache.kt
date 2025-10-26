package com.linkpoint.res

import com.linkpoint.res.executors.LoaderExecutor
import java.io.File

abstract class ResourceFileCache<ResourceParams, ResourceType> : ResourceMemoryCache<ResourceParams, ResourceType> {

    private class ResourceLoadRequest<ResParams : ResourceParams, ResType : ResourceType> : ResourceRequest<ResourceParams, ResourceType> : Runnable {
        private val File file

        public ResourceLoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file2) {
            super(resourceparams, resourceManager)
            this.file = file2
        }

        fun cancelRequest() {
            LoaderExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute() {
            LoaderExecutor.getInstance().execute(this)
        }

        fun run() {
            try {
                completeRequest(ResourceFileCache.this.createResourceFromFile(getParams(), this.file))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        val resourceFile: File = getResourceFile(resourceparams)
        return resourceFile.exists() ? ResourceLoadRequest(resourceparams, resourceManager, resourceFile) : createResourceGenRequest(resourceparams, resourceManager, resourceFile)
    }

    /* access modifiers changed from: protected */
    public abstract ResourceType createResourceFromFile(ResourceParams resourceparams, File file)

    /* access modifiers changed from: protected */
    public abstract ResourceRequest<ResourceParams, ResourceType> createResourceGenRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file)

    /* access modifiers changed from: protected */
    public abstract File getResourceFile(ResourceParams resourceparams)
}
