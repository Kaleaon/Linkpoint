// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.geometry;

import java.util.UUID;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshData;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.render.drawable.DrawableGeometry;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class GeometryCache extends ResourceMemoryCache<PrimVolumeParams, DrawableGeometry>
{
    private final MeshCache meshCache;
    
    public GeometryCache(final MeshCache meshCache) {
        this.meshCache = meshCache;
    }
    
    @Override
    protected ResourceRequest<PrimVolumeParams, DrawableGeometry> CreateNewRequest(final PrimVolumeParams primVolumeParams, final ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
        if (primVolumeParams.isMesh()) {
            return new MeshGeometryRequest(this.meshCache, primVolumeParams, resourceManager);
        }
        if (primVolumeParams.isSculpt()) {
            return new SculptGeometryRequest(primVolumeParams, resourceManager);
        }
        return new SimpleGeometryRequest(primVolumeParams, resourceManager);
    }
    
    private static class MeshGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer
    {
        private final MeshCache meshCache;
        private volatile MeshData meshData;
        
        public MeshGeometryRequest(final MeshCache meshCache, final PrimVolumeParams primVolumeParams, final ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
            this.meshCache = meshCache;
        }
        
        @Override
        public void OnResourceReady(final Object o, final boolean b) {
            if (o instanceof MeshData) {
                this.meshData = (MeshData)o;
                PrimComputeExecutor.getInstance().execute(this);
            }
            else {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(null);
            }
        }
        
        @Override
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            ((ResourceMemoryCache<UUID, ResourceType>)this.meshCache).RequestResource(((ResourceRequest<PrimVolumeParams, ResourceType>)this).getParams().SculptID, this);
        }
        
        @Override
        public void run() {
            try {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(new DrawableGeometry(this.meshData));
            }
            catch (final Exception ex) {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(null);
            }
        }
    }
    
    private static class SculptGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable, ResourceConsumer
    {
        private volatile OpenJPEG sculptData;
        private final DrawableTextureParams sculptTextureParams;
        
        public SculptGeometryRequest(final PrimVolumeParams primVolumeParams, final ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
            this.sculptTextureParams = DrawableTextureParams.create(((ResourceRequest<PrimVolumeParams, ResourceType>)this).getParams().SculptID, TextureClass.Sculpt);
        }
        
        @Override
        public void OnResourceReady(final Object o, final boolean b) {
            if (o instanceof OpenJPEG) {
                this.sculptData = (OpenJPEG)o;
                PrimComputeExecutor.getInstance().execute(this);
            }
            else {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(null);
            }
        }
        
        @Override
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            TextureCache.getInstance().CancelRequest(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            ((ResourceMemoryCache<DrawableTextureParams, ResourceType>)TextureCache.getInstance()).RequestResource(this.sculptTextureParams, this);
        }
        
        @Override
        public void run() {
            try {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(new DrawableGeometry(((ResourceRequest<PrimVolumeParams, ResourceType>)this).getParams(), this.sculptData));
            }
            catch (final Exception ex) {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(null);
            }
        }
    }
    
    private static class SimpleGeometryRequest extends ResourceRequest<PrimVolumeParams, DrawableGeometry> implements Runnable
    {
        public SimpleGeometryRequest(final PrimVolumeParams primVolumeParams, final ResourceManager<PrimVolumeParams, DrawableGeometry> resourceManager) {
            super(primVolumeParams, resourceManager);
        }
        
        @Override
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }
        
        @Override
        public void run() {
            try {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(new DrawableGeometry(((ResourceRequest<PrimVolumeParams, ResourceType>)this).getParams(), null));
            }
            catch (final Exception ex) {
                ((ResourceRequest<ResourceParams, DrawableGeometry>)this).completeRequest(null);
            }
        }
    }
}
