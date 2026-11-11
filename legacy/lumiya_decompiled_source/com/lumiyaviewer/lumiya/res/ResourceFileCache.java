package com.lumiyaviewer.lumiya.res;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.File;
/* loaded from: classes.dex */
public abstract class ResourceFileCache<ResourceParams, ResourceType> extends ResourceMemoryCache<ResourceParams, ResourceType> {

    /* loaded from: classes.dex */
    private class ResourceLoadRequest<ResParams extends ResourceParams, ResType extends ResourceType> extends ResourceRequest<ResourceParams, ResourceType> implements Runnable {
        private final File file;

        public ResourceLoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file) {
            super(resourceparams, resourceManager);
            this.file = file;
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            LoaderExecutor.getInstance().execute(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                completeRequest(ResourceFileCache.this.createResourceFromFile(getParams(), this.file));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    protected ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        File resourceFile = getResourceFile(resourceparams);
        return resourceFile.exists() ? new ResourceLoadRequest(resourceparams, resourceManager, resourceFile) : createResourceGenRequest(resourceparams, resourceManager, resourceFile);
    }

    protected abstract ResourceType createResourceFromFile(ResourceParams resourceparams, File file);

    protected abstract ResourceRequest<ResourceParams, ResourceType> createResourceGenRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file);

    protected abstract File getResourceFile(ResourceParams resourceparams);
}
