package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class GLTerrainTextureCache : GLResourceCache<TerrainPatchInfo, OpenJPEG, GLLoadedTexture> {
    private TerrainTextureCache terrainTextureCache

    constructor(gLLoadQueue: GLLoadQueue, terrainTextureCache2: TerrainTextureCache) {
        super(gLLoadQueue)
        this.terrainTextureCache = terrainTextureCache2
    }

    /* access modifiers changed from: protected */
    fun CancelRawResource(resourceConsumer: ResourceConsumer): Unit {
        this.terrainTextureCache.CancelRequest(resourceConsumer)
    }

    /* access modifiers changed from: protected */
    fun GetResourceSize(openJPEG: OpenJPEG): Int {
        return openJPEG.getLoadedSize()
    }

    /* access modifiers changed from: protected */
    fun LoadResource(terrainPatchInfo: TerrainPatchInfo, openJPEG: OpenJPEG, renderContext: RenderContext): GLLoadedTexture {
        Debug.Printf("Terrain: Loading baked texture into GL", Any[0])
        return GLLoadedTexture(renderContext, openJPEG)
    }

    /* access modifiers changed from: protected */
    fun RequestRawResource(terrainPatchInfo: TerrainPatchInfo, resourceConsumer: ResourceConsumer): Unit {
        this.terrainTextureCache.RequestResource(terrainPatchInfo, resourceConsumer)
    }
}
