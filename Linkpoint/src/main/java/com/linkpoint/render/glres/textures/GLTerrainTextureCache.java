package com.linkpoint.render.glres.textures;

import com.linkpoint.Debug;
import com.linkpoint.openjpeg.OpenJPEG;
import com.linkpoint.render.RenderContext;
import com.linkpoint.render.glres.GLLoadQueue;
import com.linkpoint.render.glres.GLResourceCache;
import com.linkpoint.res.ResourceConsumer;
import com.linkpoint.res.terrain.TerrainTextureCache;
import com.linkpoint.slproto.terrain.TerrainPatchInfo;

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
