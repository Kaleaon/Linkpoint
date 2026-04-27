package com.lumiyaviewer.lumiya.res.geometry

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams
import com.lumiyaviewer.lumiya.render.tex.TextureClass
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.ResourceManager
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache
import com.lumiyaviewer.lumiya.res.ResourceRequest
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.res.mesh.MeshCache
import com.lumiyaviewer.lumiya.res.textures.TextureCache
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams

class GeometryCache : ResourceMemoryCache<PrimVolumeParams, DrawableGeometry> {
    private MeshCache meshCache

    private class MeshGeometryRequest : ResourceRequest<PrimVolumeParams, DrawableGeometry> : Runnable, ResourceConsumer {
        private MeshCache meshCache
        private volatile MeshData meshData

        MeshGeometryRequest(MeshCache meshCache2, PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager)
            this.meshCache = meshCache2
        }

        fun OnResourceReady(obj: Any, z: Boolean): Unit {
            if (obj instanceof MeshData) {
                this.meshData = (MeshData) obj
                PrimComputeExecutor.getInstance().execute(this)
                return
            }
            completeRequest(null)
        }

        fun cancelRequest(): Unit {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            this.meshCache.RequestResource(((PrimVolumeParams) getParams()).SculptID, this)
        }

        fun run(): Unit {
            try {
                completeRequest(DrawableGeometry(this.meshData))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    private class SculptGeometryRequest : ResourceRequest<PrimVolumeParams, DrawableGeometry> : Runnable, ResourceConsumer {
        private volatile OpenJPEG sculptData
        private val sculptTextureParams: DrawableTextureParams = DrawableTextureParams.create(((PrimVolumeParams) getParams()).SculptID, TextureClass.Sculpt)

        SculptGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager)
        }

        fun OnResourceReady(obj: Any, z: Boolean): Unit {
            if (obj instanceof OpenJPEG) {
                this.sculptData = (OpenJPEG) obj
                PrimComputeExecutor.getInstance().execute(this)
                return
            }
            completeRequest(null)
        }

        fun cancelRequest(): Unit {
            PrimComputeExecutor.getInstance().remove(this)
            TextureCache.getInstance().CancelRequest(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            TextureCache.getInstance().RequestResource(this.sculptTextureParams, this)
        }

        fun run(): Unit {
            try {
                completeRequest(DrawableGeometry((PrimVolumeParams) getParams(), this.sculptData))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    private class SimpleGeometryRequest : ResourceRequest<PrimVolumeParams, DrawableGeometry> : Runnable {
        SimpleGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager)
        }

        fun cancelRequest(): Unit {
            PrimComputeExecutor.getInstance().remove(this)
            super.cancelRequest()
        }

        fun execute(): Unit {
            PrimComputeExecutor.getInstance().execute(this)
        }

        fun run(): Unit {
            try {
                completeRequest(DrawableGeometry((PrimVolumeParams) getParams(), (OpenJPEG) null))
            } catch (Exception e) {
                completeRequest(null)
            }
        }
    }

    constructor(meshCache2: MeshCache) {
        this.meshCache = meshCache2
    }

    /* access modifiers changed from: protected */
    fun CreateNewRequest(primVolumeParams: PrimVolumeParams, resourceManager: DrawableGeometry>): ResourceRequest<PrimVolumeParams, DrawableGeometry> {
        return primVolumeParams.isMesh() ? MeshGeometryRequest(this.meshCache, primVolumeParams, resourceManager) : primVolumeParams.isSculpt() ? SculptGeometryRequest(primVolumeParams, resourceManager) : SimpleGeometryRequest(primVolumeParams, resourceManager)
    }
}
