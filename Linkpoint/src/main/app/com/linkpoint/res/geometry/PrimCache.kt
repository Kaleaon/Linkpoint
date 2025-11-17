package com.linkpoint.res.geometry

import com.linkpoint.render.drawable.DrawableGeometry
import com.linkpoint.render.drawable.DrawablePrim
import com.linkpoint.render.glres.textures.GLTextureCache
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.slproto.prims.PrimDrawParams

class PrimCache : ResourceMemoryCache<PrimDrawParams, DrawablePrim> {
    private GeometryCache geometryCache
    private GLTextureCache textureCache

    private class PrimRequest : ResourceRequest<PrimDrawParams, DrawablePrim> : Runnable, ResourceConsumer {
        private volatile DrawableGeometry geometry
        private GeometryCache geometryCache
        private GLTextureCache glTextureCache

        PrimRequest(GLTextureCache gLTextureCache, GeometryCache geometryCache2, PrimDrawParams primDrawParams, ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
            super(primDrawParams, resourceManager)
            this.glTextureCache = gLTextureCache
            this.geometryCache = geometryCache2
        }

        fun OnResourceReady(obj: Any, z: Boolean): Unit {
            if (obj instanceof DrawableGeometry) {
                this.geometry = (DrawableGeometry) obj
                PrimComputeExecutor.getInstance().execute(this)
                return
            }
            completeRequest(null)
        }

        fun cancelRequest(): Unit {
            PrimComputeExecutor.getInstance().remove(this)
            this.geometryCache.CancelRequest(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            this.geometryCache.RequestResource(((PrimDrawParams) getParams()).getVolumeParams(), this)
        }

        fun run(): Unit {
            try {
                completeRequest(DrawablePrim((PrimDrawParams) getParams(), this.geometry))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    constructor(gLTextureCache: GLTextureCache, geometryCache2: GeometryCache) {
        this.textureCache = gLTextureCache
        this.geometryCache = geometryCache2
    }

    /* access modifiers changed from: protected */
    fun CreateNewRequest(primDrawParams: PrimDrawParams, resourceManager: DrawablePrim>): ResourceRequest<PrimDrawParams, DrawablePrim> {
        return PrimRequest(this.textureCache, this.geometryCache, primDrawParams, resourceManager)
    }
}
