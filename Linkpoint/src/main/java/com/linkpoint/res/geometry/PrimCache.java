package com.linkpoint.res.geometry;

import com.linkpoint.render.drawable.DrawableGeometry;
import com.linkpoint.render.drawable.DrawablePrim;
import com.linkpoint.render.glres.textures.GLTextureCache;
import com.linkpoint.res.ResourceConsumer;
import com.linkpoint.res.ResourceManager;
import com.linkpoint.res.ResourceMemoryCache;
import com.linkpoint.res.ResourceRequest;
import com.linkpoint.res.executors.PrimComputeExecutor;
import com.linkpoint.slproto.prims.PrimDrawParams;

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
