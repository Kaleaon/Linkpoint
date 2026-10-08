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
/* loaded from: classes.dex */
public class PrimCache extends ResourceMemoryCache<PrimDrawParams, DrawablePrim> {
    private final GeometryCache geometryCache;
    private final GLTextureCache textureCache;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PrimRequest extends ResourceRequest<PrimDrawParams, DrawablePrim> implements Runnable, ResourceConsumer {
        private volatile DrawableGeometry geometry;
        private final GeometryCache geometryCache;
        private final GLTextureCache glTextureCache;

        public PrimRequest(GLTextureCache gLTextureCache, GeometryCache geometryCache, PrimDrawParams primDrawParams, ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
            super(primDrawParams, resourceManager);
            this.glTextureCache = gLTextureCache;
            this.geometryCache = geometryCache;
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
        public void OnResourceReady(Object obj, boolean z) {
            if (!(obj instanceof DrawableGeometry)) {
                completeRequest(null);
                return;
            }
            this.geometry = (DrawableGeometry) obj;
            PrimComputeExecutor.getInstance().execute(this);
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            this.geometryCache.CancelRequest(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            this.geometryCache.RequestResource(getParams().getVolumeParams(), this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                completeRequest(new DrawablePrim(getParams(), this.geometry));
            } catch (Exception e) {
                completeRequest(null);
            }
        }
    }

    public PrimCache(GLTextureCache gLTextureCache, GeometryCache geometryCache) {
        this.textureCache = gLTextureCache;
        this.geometryCache = geometryCache;
    }

    protected ResourceRequest<PrimDrawParams, DrawablePrim> CreateNewRequest(PrimDrawParams primDrawParams, ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
        return new PrimRequest(this.textureCache, this.geometryCache, primDrawParams, resourceManager);
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    protected /* bridge */ /* synthetic */ ResourceRequest CreateNewRequest(Object obj, ResourceManager resourceManager) {
        return CreateNewRequest((PrimDrawParams) obj, (ResourceManager<PrimDrawParams, DrawablePrim>) resourceManager);
    }
}
