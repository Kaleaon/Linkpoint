package com.linkpoint.render.glres.textures

import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.GLLoadQueue
import com.linkpoint.render.glres.GLResourceCache
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.terrain.TerrainTextureCache
import com.linkpoint.slproto.terrain.TerrainPatchInfo

class GLTerrainTextureCache : GLResourceCache()<TerrainPatchInfo, OpenJPEG, GLLoadedTexture> {
    private val TerrainTextureCache terrainTextureCache

    public GLTerrainTextureCache(GLLoadQueue gLLoadQueue, TerrainTextureCache terrainTextureCache2) {
        super(gLLoadQueue)
        this.terrainTextureCache = terrainTextureCache2
    }

    /* access modifiers changed from: protected */
    fun CancelRawResource(ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.CancelRequest(resourceConsumer)
    }

    /* access modifiers changed from: protected */
    public Int GetResourceSize(OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize()
    }

    /* access modifiers changed from: protected */
    public GLLoadedTexture LoadResource(TerrainPatchInfo terrainPatchInfo, OpenJPEG openJPEG, RenderContext renderContext) {
        Debug.Printf("Terrain: Loading baked texture into GL", Object[0])
        return GLLoadedTexture(renderContext, openJPEG)
    }

    /* access modifiers changed from: protected */
    fun RequestRawResource(TerrainPatchInfo terrainPatchInfo, ResourceConsumer resourceConsumer) {
        this.terrainTextureCache.RequestResource(terrainPatchInfo, resourceConsumer)
    }
}
