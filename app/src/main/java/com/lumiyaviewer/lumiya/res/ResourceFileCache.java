package com.lumiyaviewer.lumiya.res;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.File;

public abstract class ResourceFileCache<ResourceParams, ResourceType> extends ResourceMemoryCache<ResourceParams, ResourceType> {

    private class ResourceLoadRequest<ResParams extends ResourceParams, ResType extends ResourceType> extends ResourceRequest<ResourceParams, ResourceType> implements Runnable {
        private final File file;

        public ResourceLoadRequest(ResourceParams resourceParams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file) {
            super(resourceParams, resourceManager);
            this.file = file;
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

    protected ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceParams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        File resourceFile = getResourceFile(resourceParams);
        return resourceFile.exists() ? new ResourceLoadRequest(resourceParams, resourceManager, resourceFile) : createResourceGenRequest(resourceParams, resourceManager, resourceFile);
    }

    protected abstract ResourceType createResourceFromFile(ResourceParams resourceParams, File file);

    protected abstract ResourceRequest<ResourceParams, ResourceType> createResourceGenRequest(ResourceParams resourceParams, ResourceManager<ResourceParams, ResourceType> resourceManager, File file);

    protected abstract File getResourceFile(ResourceParams resourceParams);
}
