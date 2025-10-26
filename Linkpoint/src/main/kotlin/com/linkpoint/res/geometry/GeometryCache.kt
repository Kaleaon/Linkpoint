package com.linkpoint.res.geometry

import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.drawable.DrawableGeometry
import com.linkpoint.render.tex.DrawableTextureParams
import com.linkpoint.render.tex.TextureClass
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.ResourceManager
import com.linkpoint.res.ResourceMemoryCache
import com.linkpoint.res.ResourceRequest
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.res.mesh.MeshCache
import com.linkpoint.res.textures.TextureCache
import com.linkpoint.slproto.mesh.MeshData
import com.linkpoint.slproto.prims.PrimVolumeParams

class GeometryCache : ResourceMemoryCache()<PrimVolumeParams, DrawableGeometry> {
    private val MeshCache meshCache

    @JvmStatic
private class MeshGeometryRequest : ResourceRequest()<PrimVolumeParams, DrawableGeometry> : Runnable, ResourceConsumer {
        private val MeshCache meshCache
        private volatile MeshData meshData

        public MeshGeometryRequest(MeshCache meshCache2, PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager)
            this.meshCache = meshCache2
        }

        fun OnResourceReady(obj: Object, z: Boolean) {
            if (obj instanceof MeshData) {
                this.meshData = (MeshData) obj
                PrimComputeExecutor.getInstance().execute(this)
                return
            }
            completeRequest(null)
        }

        fun cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute() {
            this.meshCache.RequestResource(((PrimVolumeParams) getParams()).SculptID, this)
        }

        fun run() {
            try {
                completeRequest(DrawableGeometry(this.meshData))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    @JvmStatic
private class SculptGeometryRequest : ResourceRequest()<PrimVolumeParams, DrawableGeometry> : Runnable, ResourceConsumer {
        private volatile OpenJPEG sculptData
        private val DrawableTextureParams sculptTextureParams = DrawableTextureParams.create(((PrimVolumeParams) getParams()).SculptID, TextureClass.Sculpt)

        public SculptGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager)
        }

        fun OnResourceReady(obj: Object, z: Boolean) {
            if (obj instanceof OpenJPEG) {
                this.sculptData = (OpenJPEG) obj
                PrimComputeExecutor.getInstance().execute(this)
                return
            }
            completeRequest(null)
        }

        fun cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this)
            TextureCache.getInstance().CancelRequest(this)
            super.cancelRequest()
        }

        fun execute() {
            TextureCache.getInstance().RequestResource(this.sculptTextureParams, this)
        }

        fun run() {
            try {
                completeRequest(DrawableGeometry((PrimVolumeParams) getParams(), this.sculptData))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    @JvmStatic
private class SimpleGeometryRequest : ResourceRequest()<PrimVolumeParams, DrawableGeometry> : Runnable {
        public SimpleGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager)
        }

        fun cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute() {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run() {
            try {
                completeRequest(DrawableGeometry((PrimVolumeParams) getParams(), (OpenJPEG) null))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    public GeometryCache(MeshCache meshCache2) {
        this.meshCache = meshCache2
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<PrimVolumeParams, DrawableGeometry> CreateNewRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
        return primVolumeParams.isMesh() ? MeshGeometryRequest(this.meshCache, primVolumeParams, resourceManager) : primVolumeParams.isSculpt() ? SculptGeometryRequest(primVolumeParams, resourceManager) : SimpleGeometryRequest(primVolumeParams, resourceManager)
    }
}
