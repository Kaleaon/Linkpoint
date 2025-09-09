package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;

public class GeometryCache extends ResourceMemoryCache<PrimVolumeParams, DrawableGeometry> {
    private final MeshCache meshCache;

    private static class MeshGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer {
        private final MeshCache meshCache;
        private volatile MeshData meshData;

        public MeshGeometryRequest(MeshCache meshCache, PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
            this.meshCache = meshCache;
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
                completeRequest(new DrawableGeometry((PrimVolumeParams) getParams(), null));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    public GeometryCache(MeshCache meshCache) {
        this.meshCache = meshCache;
    }

    protected ResourceRequest<PrimVolumeParams, DrawableGeometry> CreateNewRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
        return primVolumeParams.isMesh() ? new MeshGeometryRequest(this.meshCache, primVolumeParams, resourceManager) : primVolumeParams.isSculpt() ? new SculptGeometryRequest(primVolumeParams, resourceManager) : new SimpleGeometryRequest(primVolumeParams, resourceManager);
    }
}
