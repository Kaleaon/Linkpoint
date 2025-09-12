package com.lumiyaviewer.lumiya.res;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.File;

public abstract class ResourceFileCache<ResourceParams, ResourceType> extends ResourceMemoryCache<ResourceParams, ResourceType> {

    private class ResourceLoadRequest<ResParams extends ResourceParams, ResType extends ResourceType> extends ResourceRequest<ResourceParams, ResourceType> implements Runnable {
        private final File file;

        public ResourceLoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file2) {
            super(resourceparams, resourceManager);
            this.file = file2;
        }

        public void cancelRequest() {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute() {
            LoaderExecutor.getInstance().execute(this);
        }

        public void run() {
            try {
                completeRequest(ResourceFileCache.this.createResourceFromFile(getParams(), this.file));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        File resourceFile = getResourceFile(resourceparams);
        return resourceFile.exists() ? new ResourceLoadRequest(resourceparams, resourceManager, resourceFile) : createResourceGenRequest(resourceparams, resourceManager, resourceFile);
    }

    /* access modifiers changed from: protected */
    public abstract ResourceType createResourceFromFile(ResourceParams resourceparams, File file);

    /* access modifiers changed from: protected */
    public abstract ResourceRequest<ResourceParams, ResourceType> createResourceGenRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file);

    /* access modifiers changed from: protected */
    public abstract File getResourceFile(ResourceParams resourceparams);
}
