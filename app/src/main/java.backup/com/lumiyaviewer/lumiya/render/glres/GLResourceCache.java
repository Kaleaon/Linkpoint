package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;

public abstract class GLResourceCache<ResourceParams, RawType, ResourceType extends GLSizedResource> extends ResourceMemoryCache<ResourceParams, ResourceType> {
    /* access modifiers changed from: private */
    public final GLLoadQueue loadQueue;

    private class LoadRequest<Raw extends RawType> extends ResourceRequest<ResourceParams, ResourceType> implements GLLoadQueue.GLLoadable, ResourceConsumer {
        private volatile boolean finalResult;
        private volatile boolean loadedFinal;
        private volatile ResourceType loadedResource;
        private volatile Raw rawResource;

        public LoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
            super(resourceparams, resourceManager);
        }

        public void GLCompleteLoad() {
            ResourceType resourcetype;
            boolean z;
            synchronized (this) {
                resourcetype = this.loadedResource;
                z = this.loadedFinal;
            }
            if (z) {
                completeRequest(resourcetype);
            } else {
                intermediateResult(resourcetype);
            }
        }

        public int GLGetLoadSize() {
            Raw raw;
            synchronized (this) {
                raw = this.rawResource;
            }
            if (raw != null) {
                return GLResourceCache.this.GetResourceSize(raw);
            }
            return 0;
        }

        public int GLLoad(RenderContext renderContext, GLLoadQueue.GLLoadHandler gLLoadHandler) {
            Raw raw;
            boolean z;
            synchronized (this) {
                raw = this.rawResource;
                z = this.finalResult;
            }
            ResourceType LoadResource = GLResourceCache.this.LoadResource(getParams(), raw, renderContext);
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
                        this.finalResult = !z;
                    }
                    GLResourceCache.this.loadQueue.add(this);
                } catch (ClassCastException e) {
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

    /* access modifiers changed from: protected */
    public abstract void CancelRawResource(ResourceConsumer resourceConsumer);

    /* access modifiers changed from: protected */
    public ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        return new LoadRequest(resourceparams, resourceManager);
    }

    /* access modifiers changed from: protected */
    public abstract int GetResourceSize(RawType rawtype);

    /* access modifiers changed from: protected */
    public abstract ResourceType LoadResource(ResourceParams resourceparams, RawType rawtype, RenderContext renderContext);

    /* access modifiers changed from: protected */
    public abstract void RequestRawResource(ResourceParams resourceparams, ResourceConsumer resourceConsumer);
}
