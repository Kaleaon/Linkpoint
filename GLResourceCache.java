package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;

public abstract class GLResourceCache<ResourceParams, RawType, ResourceType extends GLSizedResource> extends ResourceMemoryCache<ResourceParams, ResourceType> {
    private final GLLoadQueue loadQueue;

    private class LoadRequest<Raw extends RawType> extends ResourceRequest<ResourceParams, ResourceType> implements GLLoadable, ResourceConsumer {
        private volatile boolean finalResult;
        private volatile boolean loadedFinal;
        private volatile ResourceType loadedResource;
        private volatile Raw rawResource;

        public LoadRequest(ResourceParams resourceParams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
            super(resourceParams, resourceManager);
        }

        public void GLCompleteLoad() {
            GLSizedResource gLSizedResource;
            boolean z;
            synchronized (this) {
                gLSizedResource = this.loadedResource;
                z = this.loadedFinal;
            }
            if (z) {
                completeRequest(gLSizedResource);
            } else {
                intermediateResult(gLSizedResource);
            }
        }

        public int GLGetLoadSize() {
            Object obj;
            synchronized (this) {
                obj = this.rawResource;
            }
            return obj != null ? GLResourceCache.this.GetResourceSize(obj) : 0;
        }

        public int GLLoad(RenderContext renderContext, GLLoadHandler gLLoadHandler) {
            Object obj;
            boolean z;
            synchronized (this) {
                obj = this.rawResource;
                z = this.finalResult;
            }
            GLSizedResource LoadResource = GLResourceCache.this.LoadResource(getParams(), obj, renderContext);
            int loadedSize = LoadResource != null ? LoadResource.getLoadedSize() : 0;
            synchronized (this) {
                this.loadedResource = LoadResource;
                this.loadedFinal = z;
            }
            if (LoadResource != null) {
                gLLoadHandler.GLResourceLoaded(this);
            }
            return loadedSize;
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj != null) {
                try {
                    synchronized (this) {
                        this.rawResource = obj;
                        this.finalResult = z ^ 1;
                    }
                    GLResourceCache.this.loadQueue.add(this);
                } catch (Throwable e) {
                    Debug.Warning(e);
                    completeRequest(null);
                }
            } else {
                completeRequest(null);
            }
            GLResourceCache.this.collectReferences();
        }

        public void cancelRequest() {
            GLResourceCache.this.loadQueue.remove(this);
            GLResourceCache.this.CancelRawResource(this);
            super.cancelRequest();
        }

        public void execute() {
            GLResourceCache.this.RequestRawResource(getParams(), this);
        }
    }

    protected GLResourceCache(GLLoadQueue gLLoadQueue) {
        this.loadQueue = gLLoadQueue;
    }

    protected abstract void CancelRawResource(ResourceConsumer resourceConsumer);

    protected ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceParams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        return new LoadRequest(resourceParams, resourceManager);
    }

    protected abstract int GetResourceSize(RawType rawType);

    protected abstract ResourceType LoadResource(ResourceParams resourceParams, RawType rawType, RenderContext renderContext);

    protected abstract void RequestRawResource(ResourceParams resourceParams, ResourceConsumer resourceConsumer);
}
