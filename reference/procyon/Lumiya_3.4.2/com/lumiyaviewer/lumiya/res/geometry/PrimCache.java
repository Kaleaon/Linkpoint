// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.geometry;

import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.drawable.DrawablePrim;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class PrimCache extends ResourceMemoryCache<PrimDrawParams, DrawablePrim>
{
    private final GeometryCache geometryCache;
    private final GLTextureCache textureCache;
    
    public PrimCache(final GLTextureCache textureCache, final GeometryCache geometryCache) {
        this.textureCache = textureCache;
        this.geometryCache = geometryCache;
    }
    
    @Override
    protected ResourceRequest<PrimDrawParams, DrawablePrim> CreateNewRequest(final PrimDrawParams primDrawParams, final ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
        return new PrimRequest(this.textureCache, this.geometryCache, primDrawParams, resourceManager);
    }
    
    private static class PrimRequest extends ResourceRequest<PrimDrawParams, DrawablePrim> implements Runnable, ResourceConsumer
    {
        private volatile DrawableGeometry geometry;
        private final GeometryCache geometryCache;
        private final GLTextureCache glTextureCache;
        
        public PrimRequest(final GLTextureCache glTextureCache, final GeometryCache geometryCache, final PrimDrawParams primDrawParams, final ResourceManager<PrimDrawParams, DrawablePrim> resourceManager) {
            super(primDrawParams, resourceManager);
            this.glTextureCache = glTextureCache;
            this.geometryCache = geometryCache;
        }
        
        @Override
        public void OnResourceReady(final Object o, final boolean b) {
            if (o instanceof DrawableGeometry) {
                this.geometry = (DrawableGeometry)o;
                PrimComputeExecutor.getInstance().execute(this);
            }
            else {
                ((ResourceRequest<ResourceParams, DrawablePrim>)this).completeRequest(null);
            }
        }
        
        @Override
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            this.geometryCache.CancelRequest(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            ((ResourceMemoryCache<PrimVolumeParams, ResourceType>)this.geometryCache).RequestResource(((ResourceRequest<PrimDrawParams, ResourceType>)this).getParams().getVolumeParams(), this);
        }
        
        @Override
        public void run() {
            try {
                ((ResourceRequest<ResourceParams, DrawablePrim>)this).completeRequest(new DrawablePrim(((ResourceRequest<PrimDrawParams, ResourceType>)this).getParams(), this.geometry));
            }
            catch (final Exception ex) {
                ((ResourceRequest<ResourceParams, DrawablePrim>)this).completeRequest(null);
            }
        }
    }
}
