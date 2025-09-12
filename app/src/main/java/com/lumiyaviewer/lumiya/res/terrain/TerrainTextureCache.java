package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.util.UUID;
import java.util.concurrent.Future;

public class TerrainTextureCache extends ResourceMemoryCache<TerrainPatchInfo, OpenJPEG> {
    public static final int TextureResolution = 256;

    private static class TerrainTextureRequest extends ResourceRequest<TerrainPatchInfo, OpenJPEG> implements Runnable, HasPriority {
        private volatile Future<?> bakingFuture = null;
        private int layerNeededMask;
        private int layerReadyMask;
        private final TerrainRawTextureRequest[] rawRequests = new TerrainRawTextureRequest[4];
        private final OpenJPEG[] rawTextures = new OpenJPEG[4];

        private class TerrainRawTextureRequest implements ResourceConsumer {
            private final int layer;

            public TerrainRawTextureRequest(UUID uuid, int i) {
                this.layer = i;
                TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuid, TextureClass.Terrain), this);
            }

            public void OnResourceReady(Object obj, boolean z) {
                if (obj instanceof OpenJPEG) {
                    TerrainTextureRequest.this.onLayerReady(this.layer, (OpenJPEG) obj);
                } else if (obj == null) {
                    TerrainTextureRequest.this.onLayerReady(this.layer, (OpenJPEG) null);
                }
            }
        }

        public TerrainTextureRequest(TerrainPatchInfo terrainPatchInfo, ResourceManager<TerrainPatchInfo, OpenJPEG> resourceManager) {
            super(terrainPatchInfo, resourceManager);
        }

        public void cancelRequest() {
            synchronized (this) {
                for (int i = 0; i < 4; i++) {
                    this.rawRequests[i] = null;
                }
                Future<?> future = this.bakingFuture;
                if (future != null) {
                    future.cancel(false);
                }
            }
            super.cancelRequest();
        }

        public void execute() {
            this.layerNeededMask = ((TerrainPatchInfo) getParams()).getLayerMask();
            this.layerReadyMask = 0;
            if (this.layerNeededMask != 0) {
                synchronized (this) {
                    for (int i = 0; i < 4; i++) {
                        if (this.rawRequests[i] == null && (this.layerNeededMask & (1 << i)) != 0) {
                            this.rawRequests[i] = new TerrainRawTextureRequest(((TerrainPatchInfo) getParams()).getTextures().getTextureUUID(i), i);
                        }
                    }
                }
                return;
            }
            this.bakingFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this);
        }

        public int getPriority() {
            return 0;
        }

        /* access modifiers changed from: protected */
        public synchronized void onLayerReady(int i, OpenJPEG openJPEG) {
            boolean z = false;
            synchronized (this) {
                this.rawTextures[i] = openJPEG;
                this.layerReadyMask |= 1 << i;
                Object[] objArr = new Object[4];
                objArr[0] = Integer.valueOf(i);
                objArr[1] = openJPEG != null ? openJPEG.toString() : "null";
                objArr[2] = Integer.valueOf(this.layerNeededMask);
                objArr[3] = Integer.valueOf(this.layerReadyMask);
                Debug.Printf("Terrain: onLayerReady (%d), rawTexture %s, layerNeededMask %d, layerReadyMask %d", objArr);
                if ((this.layerNeededMask & this.layerReadyMask) == this.layerNeededMask) {
                    int i2 = 0;
                    while (true) {
                        if (i2 < 4) {
                            if ((this.layerNeededMask & (1 << i2)) != 0 && this.rawTextures[i2] == null) {
                                Debug.Printf("Terrain: texture for layer %d is not ready", Integer.valueOf(i2));
                                break;
                            }
                            i2++;
                        } else {
                            z = true;
                            break;
                        }
                    }
                    if (z) {
                        this.bakingFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this);
                    } else {
                        completeRequest(null);
                    }
                }
            }
        }

        public void run() {
            try {
                TerrainPatchInfo terrainPatchInfo = (TerrainPatchInfo) getParams();
                OpenJPEG bakeTerrain = OpenJPEG.bakeTerrain(256, 256, this.rawTextures, terrainPatchInfo.getTextureHeightMap(), terrainPatchInfo.getHeightMap().getMapWidth(), terrainPatchInfo.getHeightMap().getMapHeight());
                Debug.Printf("Terrain: Baked texture producer: produced baked texture", new Object[0]);
                completeRequest(bakeTerrain);
            } catch (Exception e) {
                Debug.Warning(e);
                completeRequest(null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<TerrainPatchInfo, OpenJPEG> CreateNewRequest(TerrainPatchInfo terrainPatchInfo, ResourceManager<TerrainPatchInfo, OpenJPEG> resourceManager) {
        return new TerrainTextureRequest(terrainPatchInfo, resourceManager);
    }
}
