package com.lumiyaviewer.lumiya.render.glres

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest

abstract class GLResourceCache<ResourceParams, RawType, ResourceType extends GLSizedResource> extends ResourceMemoryCache<ResourceParams, ResourceType> {
    /* access modifiers changed from: private */
    GLLoadQueue loadQueue

    private class LoadRequest<Raw extends RawType> extends ResourceRequest<ResourceParams, ResourceType> implements GLLoadQueue.GLLoadable, ResourceConsumer {
        private volatile boolean finalResult
        private volatile boolean loadedFinal
        private volatile ResourceType loadedResource
        private volatile Raw rawResource

        LoadRequest(ResourceParams resourceparams, ResourceManager<ResourceParams, ResourceType> resourceManager) {
            super(resourceparams, resourceManager)
        }

        fun GLCompleteLoad(): Unit {
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

        fun GLGetLoadSize(): Int {
            Raw raw
            synchronized (this) {
                raw = this.rawResource
            }
            if (raw != null) {
                return GLResourceCache.this.GetResourceSize(raw)
            }
            return 0
        }

        fun GLLoad(renderContext: RenderContext, gLLoadHandler: GLLoadQueue.GLLoadHandler): Int {
            Raw raw
            synchronized (this) {
                raw = this.rawResource
                z = this.finalResult
            }
            ResourceType LoadResource = GLResourceCache.this.LoadResource(getParams(), raw, renderContext)
            int loadedSize = LoadResource != null ? LoadResource.getLoadedSize() : 0
            synchronized (this) {
                this.loadedResource = LoadResource
                this.loadedFinal = z
            }
            if (LoadResource != null) {
                gLLoadHandler.GLResourceLoaded(this)
            }
            return loadedSize
        }

        fun OnResourceReady(obj: Any, z: Boolean): Unit {
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

        fun cancelRequest(): Unit {
            GLResourceCache.this.loadQueue.remove(this)
            GLResourceCache.this.CancelRawResource(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            GLResourceCache.this.RequestRawResource(getParams(), this)
        }
    }

    protected GLResourceCache(GLLoadQueue gLLoadQueue) {
        this.loadQueue = gLLoadQueue
    }

    /* access modifiers changed from: protected */
    abstract fun CancelRawResource(resourceConsumer: ResourceConsumer): Unit

    /* access modifiers changed from: protected */
    fun CreateNewRequest(resourceparams: ResourceParams, resourceManager: ResourceType>): ResourceRequest<ResourceParams, ResourceType> {
        return LoadRequest(resourceparams, resourceManager)
    }

    /* access modifiers changed from: protected */
    abstract fun GetResourceSize(rawtype: RawType): Int

    /* access modifiers changed from: protected */
    abstract fun LoadResource(resourceparams: ResourceParams, rawtype: RawType, renderContext: RenderContext): ResourceType

    /* access modifiers changed from: protected */
    abstract fun RequestRawResource(resourceparams: ResourceParams, resourceConsumer: ResourceConsumer): Unit
}
