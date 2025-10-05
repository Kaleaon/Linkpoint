package com.linkpoint.res.geometry;

import com.linkpoint.openjpeg.OpenJPEG;
import com.linkpoint.render.drawable.DrawableGeometry;
import com.linkpoint.render.tex.DrawableTextureParams;
import com.linkpoint.render.tex.TextureClass;
import com.linkpoint.res.ResourceConsumer;
import com.linkpoint.res.ResourceManager;
import com.linkpoint.res.ResourceMemoryCache;
import com.linkpoint.res.ResourceRequest;
import com.linkpoint.res.executors.PrimComputeExecutor;
import com.linkpoint.res.mesh.MeshCache;
import com.linkpoint.res.textures.TextureCache;
import com.linkpoint.slproto.mesh.MeshData;
import com.linkpoint.slproto.prims.PrimVolumeParams;

public class GeometryCache extends ResourceMemoryCache<PrimVolumeParams, DrawableGeometry> {
    private final MeshCache meshCache;

    private static class MeshGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer {
        private final MeshCache meshCache;
        private volatile MeshData meshData;

        public MeshGeometryRequest(MeshCache meshCache2, PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
            this.meshCache = meshCache2;
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof MeshData) {
                this.meshData = (MeshData) obj;
                PrimComputeExecutor.getInstance().execute(this);
                return;
            }
            completeRequest(null);
        }

        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute() {
            this.meshCache.RequestResource(((PrimVolumeParams) getParams()).SculptID, this);
        }

        public void run() {
            try {
                completeRequest(new DrawableGeometry(this.meshData));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    private static class SculptGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer {
        private volatile OpenJPEG sculptData;
        private final DrawableTextureParams sculptTextureParams = DrawableTextureParams.create(((PrimVolumeParams) getParams()).SculptID, TextureClass.Sculpt);

        public SculptGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof OpenJPEG) {
                this.sculptData = (OpenJPEG) obj;
                PrimComputeExecutor.getInstance().execute(this);
                return;
            }
            completeRequest(null);
        }

        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            TextureCache.getInstance().CancelRequest(this);
            super.cancelRequest();
        }

        public void execute() {
            TextureCache.getInstance().RequestResource(this.sculptTextureParams, this);
        }

        public void run() {
            try {
                completeRequest(new DrawableGeometry((PrimVolumeParams) getParams(), this.sculptData));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    private static class SimpleGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable {
        public SimpleGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
        }

        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }

        public void run() {
            try {
                completeRequest(new DrawableGeometry((PrimVolumeParams) getParams(), (OpenJPEG) null));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    public GeometryCache(MeshCache meshCache2) {
        this.meshCache = meshCache2;
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<PrimVolumeParams, DrawableGeometry> CreateNewRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
        return primVolumeParams.isMesh() ? new MeshGeometryRequest(this.meshCache, primVolumeParams, resourceManager) : primVolumeParams.isSculpt() ? new SculptGeometryRequest(primVolumeParams, resourceManager) : new SimpleGeometryRequest(primVolumeParams, resourceManager);
    }
}
