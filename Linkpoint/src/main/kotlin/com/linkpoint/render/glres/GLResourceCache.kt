package com.linkpoint.render.glres

import com.linkpoint.Debug
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLLoadQueue
import com.linkpoint.render.glres.GLSizedResource
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest

abstract class GLResourceCache<ResourceParams, RawType, ResourceType : GLSizedResource> : ResourceMemoryCache<ResourceParams, ResourceType> {
    /* access modifiers changed from: private */
    val GLLoadQueue loadQueue

    private class LoadRequest<Raw : RawType> : ResourceRequest<ResourceParams, ResourceType> : GLLoadQueue.GLLoadable, ResourceConsumer {
        private volatile Boolean finalResult
        private volatile Boolean loadedFinal
        private volatile ResourceType loadedResource
        private volatile Raw rawResource

        public LoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
            super(resourceparams, resourceManager)
        }

        fun GLCompleteLoad() {
            ResourceType resourcetype
            synchronized (this) {
                resourcetype = this.loadedResource
                z = this.loadedFinal
            }
            if (z) {
                completeRequest(resourcetype)
            } else {
                intermediateResult(resourcetype)
            }
        }

        public Int GLGetLoadSize() {
            Raw raw
            synchronized (this) {
                raw = this.rawResource
            }
            if (raw != null) {
                return GLResourceCache.this.GetResourceSize(raw)
            }
            return 0
        }

        public Int GLLoad(RenderContext renderContext, GLLoadQueue.GLLoadHandler gLLoadHandler) {
            Raw raw
            synchronized (this) {
                raw = this.rawResource
                z = this.finalResult
            }
            ResourceType LoadResource = GLResourceCache.this.LoadResource(getParams(), raw, renderContext)
            Int loadedSize = LoadResource != null ? LoadResource.getLoadedSize() : 0
            synchronized (this) {
                this.loadedResource = LoadResource
                this.loadedFinal = z
            }
            if (LoadResource != null) {
                gLLoadHandler.GLResourceLoaded(this)
            }
            return loadedSize
        }

        fun OnResourceReady(Object obj, Boolean z) {
            if (obj != null) {
                try {
                    synchronized (this) {
                        this.rawResource = obj
                        this.finalResult = !z
                    }
                    GLResourceCache.this.loadQueue.add(this)
                } catch (ClassCastException e) {
                    Debug.Warning(e)
                    completeRequest(null)
                }
            } else {
                completeRequest(null)
            }
            GLResourceCache.this.collectReferences()
        }

        fun cancelRequest() {
            GLResourceCache.this.loadQueue.remove(this)
            GLResourceCache.this.CancelRawResource(this)
            super.cancelRequest()
        }

        fun execute() {
            GLResourceCache.this.RequestRawResource(getParams(), this)
        }
    }

    protected GLResourceCache(GLLoadQueue gLLoadQueue) {
        this.loadQueue = gLLoadQueue
    }

    /* access modifiers changed from: protected */
    public abstract Unit CancelRawResource(ResourceConsumer resourceConsumer)

    /* access modifiers changed from: protected */
    public ResourceRequest<ResourceParams, ResourceType> CreateNewRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
        return LoadRequest(resourceparams, resourceManager)
    }

    /* access modifiers changed from: protected */
    public abstract Int GetResourceSize(RawType rawtype)

    /* access modifiers changed from: protected */
    public abstract ResourceType LoadResource(ResourceParams resourceparams, RawType rawtype, RenderContext renderContext)

    /* access modifiers changed from: protected */
    public abstract Unit RequestRawResource(ResourceParams resourceparams, ResourceConsumer resourceConsumer)
}
