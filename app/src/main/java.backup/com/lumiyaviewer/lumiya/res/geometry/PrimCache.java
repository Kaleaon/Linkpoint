package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.render.drawable.DrawablePrim;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;

public class PrimCache extends ResourceMemoryCache<PrimDrawParams, DrawablePrim> {
    private final GeometryCache geometryCache;
    private final GLTextureCache textureCache;

    private static class PrimRequest extends ResourceRequest<PrimDrawParams, DrawablePrim> implements Runnable, ResourceConsumer {
        private volatile DrawableGeometry geometry;
        private final GeometryCache geometryCache;
        private final GLTextureCache glTextureCache;

        public PrimRequest(GLTextureCache gLTextureCache, GeometryCache geometryCache2, PrimDrawParams primDrawParams, ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
            super(primDrawParams, resourceManager);
            this.glTextureCache = gLTextureCache;
            this.geometryCache = geometryCache2;
        }

        public void OnResourceReady(Object obj, boolean z) {
            if (obj instanceof DrawableGeometry) {
                this.geometry = (DrawableGeometry) obj;
                PrimComputeExecutor.getInstance().execute(this);
                return;
            }
            completeRequest(null);
        }

        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            this.geometryCache.CancelRequest(this);
            super.cancelRequest();
        }

        public void execute() {
            this.geometryCache.RequestResource(((PrimDrawParams) getParams()).getVolumeParams(), this);
        }

        public void run() {
            try {
                completeRequest(new DrawablePrim((PrimDrawParams) getParams(), this.geometry));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    public PrimCache(GLTextureCache gLTextureCache, GeometryCache geometryCache2) {
        this.textureCache = gLTextureCache;
        this.geometryCache = geometryCache2;
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<PrimDrawParams, DrawablePrim> CreateNewRequest(PrimDrawParams primDrawParams, ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
        return new PrimRequest(this.textureCache, this.geometryCache, primDrawParams, resourceManager);
    }
}
