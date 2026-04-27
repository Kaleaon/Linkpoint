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
    fun CancelRawResource(resourceConsumer: ResourceConsumer) {
        this.terrainTextureCache.CancelRequest(resourceConsumer)
    }

    /* access modifiers changed from: protected */
    public fun GetResourceSize(openJPEG: OpenJPEG): Int {
        return openJPEG.getLoadedSize()
    }

    /* access modifiers changed from: protected */
    public fun LoadResource(terrainPatchInfo: TerrainPatchInfo, openJPEG: OpenJPEG, renderContext: RenderContext): GLLoadedTexture {
        Debug.Printf("Terrain: Loading baked texture into GL", Object[0])
        return GLLoadedTexture(renderContext, openJPEG)
    }

    /* access modifiers changed from: protected */
    fun RequestRawResource(terrainPatchInfo: TerrainPatchInfo, resourceConsumer: ResourceConsumer) {
        this.terrainTextureCache.RequestResource(terrainPatchInfo, resourceConsumer)
    }
}
