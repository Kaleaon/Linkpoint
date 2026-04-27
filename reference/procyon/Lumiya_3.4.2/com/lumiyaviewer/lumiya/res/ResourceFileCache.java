// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.File;

public abstract class ResourceFileCache<ResourceParams, ResourceType> extends ResourceMemoryCache<ResourceParams, ResourceType>
{
    @Override
    protected ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(final ResourceParams resourceParams, final ResourceManager<ResourceParams, ResourceType> resourceManager) {
        final File resourceFile = this.getResourceFile(resourceParams);
        if (resourceFile.exists()) {
            return (ResourceRequest<ResourceParams, ResourceType>)new ResourceLoadRequest<Object, Object>(resourceParams, resourceManager, resourceFile);
        }
        return this.createResourceGenRequest(resourceParams, resourceManager, resourceFile);
    }
    
    protected abstract ResourceType createResourceFromFile(final ResourceParams p0, final File p1);
    
    protected abstract ResourceRequest<ResourceParams, ResourceType> createResourceGenRequest(final ResourceParams p0, final ResourceManager<ResourceParams, ResourceType> p1, final File p2);
    
    protected abstract File getResourceFile(final ResourceParams p0);
    
    private class ResourceLoadRequest<ResParams extends ResourceParams, ResType extends ResourceType> extends ResourceRequest<ResourceParams, ResourceType> implements Runnable
    {
        private final File file;
        
        public ResourceLoadRequest(final ResourceParams resourceParams, final ResourceManager<ResourceParams, ResourceType> resourceManager, final File file) {
            super(resourceParams, resourceManager);
            this.file = file;
        }
        
        @Override
        public void cancelRequest() {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            LoaderExecutor.getInstance().execute(this);
        }
        
        @Override
        public void run() {
            try {
                this.completeRequest(ResourceFileCache.this.createResourceFromFile(this.getParams(), this.file));
            }
            catch (final Exception ex) {
                this.completeRequest(null);
            }
        }
    }
}
