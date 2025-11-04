// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.util.UUID;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import java.util.concurrent.Future;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class TerrainTextureCache extends ResourceMemoryCache<TerrainPatchInfo, OpenJPEG>
{
    public static final int TextureResolution = 256;
    
    @Override
    protected ResourceRequest<TerrainPatchInfo, OpenJPEG> CreateNewRequest(final TerrainPatchInfo terrainPatchInfo, final ResourceManager<TerrainPatchInfo, OpenJPEG> resourceManager) {
        return new TerrainTextureRequest(terrainPatchInfo, resourceManager);
    }
    
    private static class TerrainTextureRequest extends ResourceRequest<TerrainPatchInfo, OpenJPEG> implements Runnable, HasPriority
    {
        private volatile Future<?> bakingFuture;
        private int layerNeededMask;
        private int layerReadyMask;
        private final TerrainRawTextureRequest[] rawRequests;
        private final OpenJPEG[] rawTextures;
        
        public TerrainTextureRequest(final TerrainPatchInfo terrainPatchInfo, final ResourceManager<TerrainPatchInfo, OpenJPEG> resourceManager) {
            super(terrainPatchInfo, resourceManager);
            this.rawRequests = new TerrainRawTextureRequest[4];
            this.rawTextures = new OpenJPEG[4];
            this.bakingFuture = null;
        }
        
        @Override
        public void cancelRequest() {
            int n = 0;
            monitorenter(this);
        Block_3_Outer:
            while (true) {
                Label_0022: {
                    if (n >= 4) {
                        break Label_0022;
                    }
                    try {
                        this.rawRequests[n] = null;
                        ++n;
                        continue Block_3_Outer;
                        while (true) {
                            final Future<?> bakingFuture;
                            bakingFuture.cancel(false);
                            Label_0039: {
                                monitorexit(this);
                            }
                            super.cancelRequest();
                            return;
                            bakingFuture = this.bakingFuture;
                            iftrue(Label_0039:)(bakingFuture == null);
                            continue;
                        }
                    }
                    finally {
                        monitorexit(this);
                    }
                }
            }
        }
        
        @Override
        public void execute() {
            int n = 0;
            this.layerNeededMask = ((ResourceRequest<TerrainPatchInfo, ResourceType>)this).getParams().getLayerMask();
            this.layerReadyMask = 0;
            Label_0098: {
                if (this.layerNeededMask == 0) {
                    break Label_0098;
                }
                monitorenter(this);
                while (true) {
                    if (n >= 4) {
                        return;
                    }
                    try {
                        if (this.rawRequests[n] == null && (this.layerNeededMask & 1 << n) != 0x0) {
                            this.rawRequests[n] = new TerrainRawTextureRequest(((ResourceRequest<TerrainPatchInfo, ResourceType>)this).getParams().getTextures().getTextureUUID(n), n);
                        }
                        ++n;
                        continue;
                    }
                    finally {
                        monitorexit(this);
                    }
                    break;
                }
            }
            this.bakingFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this);
        }
        
        @Override
        public int getPriority() {
            return 0;
        }
        
        protected void onLayerReady(int i, final OpenJPEG openJPEG) {
            while (true) {
                final int n = 0;
                while (true) {
                    Label_0181: {
                        synchronized (this) {
                            this.rawTextures[i] = openJPEG;
                            this.layerReadyMask |= 1 << i;
                            String string;
                            if (openJPEG != null) {
                                string = openJPEG.toString();
                            }
                            else {
                                string = "null";
                            }
                            Debug.Printf("Terrain: onLayerReady (%d), rawTexture %s, layerNeededMask %d, layerReadyMask %d", i, string, this.layerNeededMask, this.layerReadyMask);
                            if ((this.layerNeededMask & this.layerReadyMask) == this.layerNeededMask) {
                                Block_7: {
                                    for (i = 0; i < 4; ++i) {
                                        if ((this.layerNeededMask & 1 << i) != 0x0 && this.rawTextures[i] == null) {
                                            break Block_7;
                                        }
                                    }
                                    break Label_0181;
                                }
                                Debug.Printf("Terrain: texture for layer %d is not ready", i);
                                i = n;
                                if (i != 0) {
                                    this.bakingFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this);
                                }
                                else {
                                    ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(null);
                                }
                            }
                            return;
                        }
                    }
                    i = 1;
                    continue;
                }
            }
        }
        
        @Override
        public void run() {
            try {
                final TerrainPatchInfo terrainPatchInfo = ((ResourceRequest<TerrainPatchInfo, ResourceType>)this).getParams();
                final OpenJPEG bakeTerrain = OpenJPEG.bakeTerrain(256, 256, this.rawTextures, terrainPatchInfo.getTextureHeightMap(), terrainPatchInfo.getHeightMap().getMapWidth(), terrainPatchInfo.getHeightMap().getMapHeight());
                Debug.Printf("Terrain: Baked texture producer: produced baked texture", new Object[0]);
                ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(bakeTerrain);
            }
            catch (final Exception ex) {
                Debug.Warning(ex);
                ((ResourceRequest<ResourceParams, OpenJPEG>)this).completeRequest(null);
            }
        }
        
        private class TerrainRawTextureRequest implements ResourceConsumer
        {
            private final int layer;
            
            public TerrainRawTextureRequest(final UUID uuid, final int layer) {
                this.layer = layer;
                ((ResourceMemoryCache<DrawableTextureParams, ResourceType>)TextureCache.getInstance()).RequestResource(DrawableTextureParams.create(uuid, TextureClass.Terrain), this);
            }
            
            @Override
            public void OnResourceReady(final Object o, final boolean b) {
                if (o instanceof OpenJPEG) {
                    TerrainTextureRequest.this.onLayerReady(this.layer, (OpenJPEG)o);
                }
                else if (o == null) {
                    TerrainTextureRequest.this.onLayerReady(this.layer, null);
                }
            }
        }
    }
}
