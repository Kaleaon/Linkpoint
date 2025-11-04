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

/* loaded from: classes.dex */
public class GeometryCache extends ResourceMemoryCache<PrimVolumeParams, DrawableGeometry> {
    private final MeshCache meshCache;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MeshGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer {
        private final MeshCache meshCache;
        private volatile MeshData meshData;

        public MeshGeometryRequest(MeshCache meshCache, PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
            this.meshCache = meshCache;
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
        public void OnResourceReady(Object obj, boolean z) {
            if (!(obj instanceof MeshData)) {
                completeRequest(null);
            } else {
                this.meshData = (MeshData) obj;
                PrimComputeExecutor.getInstance().execute(this);
            }
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            this.meshCache.RequestResource(getParams().SculptID, this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                completeRequest(new DrawableGeometry(this.meshData));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SculptGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer {
        private volatile OpenJPEG sculptData;
        private final DrawableTextureParams sculptTextureParams;

        public SculptGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
            this.sculptTextureParams = DrawableTextureParams.create(getParams().SculptID, TextureClass.Sculpt);
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
        public void OnResourceReady(Object obj, boolean z) {
            if (!(obj instanceof OpenJPEG)) {
                completeRequest(null);
            } else {
                this.sculptData = (OpenJPEG) obj;
                PrimComputeExecutor.getInstance().execute(this);
            }
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            TextureCache.getInstance().CancelRequest(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            TextureCache.getInstance().RequestResource(this.sculptTextureParams, this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                completeRequest(new DrawableGeometry(getParams(), this.sculptData));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SimpleGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable {
        public SimpleGeometryRequest(PrimVolumeParams primVolumeParams, ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                completeRequest(new DrawableGeometry(getParams(), null));
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

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    protected /* bridge */ /* synthetic */ ResourceRequest CreateNewRequest(Object obj, ResourceManager resourceManager) {
        return CreateNewRequest((PrimVolumeParams) obj, (ResourceManager<PrimVolumeParams, DrawableGeometry>) resourceManager);
    }
}
