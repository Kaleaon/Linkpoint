package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;

/* loaded from: classes.dex */
public class GLTerrainTextureCache extends GLResourceCache<TerrainPatchInfo, OpenJPEG, GLLoadedTexture> {
    private final TerrainTextureCache terrainTextureCache;

    public GLTerrainTextureCache(GLLoadQueue gLLoadQueue, TerrainTextureCache terrainTextureCache) {
        super(gLLoadQueue);
        this.terrainTextureCache = terrainTextureCache;
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    protected void CancelRawResource(ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.CancelRequest(resourceConsumer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    public int GetResourceSize(OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    public GLLoadedTexture LoadResource(TerrainPatchInfo terrainPatchInfo, OpenJPEG openJPEG, RenderContext renderContext) {
        Debug.Printf("Terrain: Loading baked texture into GL", new Object[0]);
        return new GLLoadedTexture(renderContext, openJPEG);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    public void RequestRawResource(TerrainPatchInfo terrainPatchInfo, ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.RequestResource(terrainPatchInfo, resourceConsumer);
    }
}
