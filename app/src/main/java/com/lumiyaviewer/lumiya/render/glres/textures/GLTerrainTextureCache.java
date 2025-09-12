package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;

public class GLTerrainTextureCache extends GLResourceCache<TerrainPatchInfo, OpenJPEG, GLLoadedTexture> {
    private final TerrainTextureCache terrainTextureCache;

    public GLTerrainTextureCache(GLLoadQueue gLLoadQueue, TerrainTextureCache terrainTextureCache2) {
        super(gLLoadQueue);
        this.terrainTextureCache = terrainTextureCache2;
    }

    /* access modifiers changed from: protected */
    public void CancelRawResource(ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.CancelRequest(resourceConsumer);
    }

    /* access modifiers changed from: protected */
    public int GetResourceSize(OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize();
    }

    /* access modifiers changed from: protected */
    public GLLoadedTexture LoadResource(TerrainPatchInfo terrainPatchInfo, OpenJPEG openJPEG, RenderContext renderContext) {
        Debug.Printf("Terrain: Loading baked texture into GL", new Object[0]);
        return new GLLoadedTexture(renderContext, openJPEG);
    }

    /* access modifiers changed from: protected */
    public void RequestRawResource(TerrainPatchInfo terrainPatchInfo, ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.RequestResource(terrainPatchInfo, resourceConsumer);
    }
}
