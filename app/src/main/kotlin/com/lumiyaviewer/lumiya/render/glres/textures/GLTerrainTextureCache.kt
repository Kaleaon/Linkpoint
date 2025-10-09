package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class GLTerrainTextureCache(
    glLoadQueue: GLLoadQueue,
    private val terrainTextureCache: TerrainTextureCache
) : GLResourceCache<TerrainPatchInfo, OpenJPEG, GLLoadedTexture>(glLoadQueue) {

    override fun CancelRawResource(resourceConsumer: ResourceConsumer) {
        terrainTextureCache.CancelRequest(resourceConsumer)
    }

    override fun GetResourceSize(rawResource: OpenJPEG): Int {
        return rawResource.loadedSize
    }

    override fun LoadResource(
        params: TerrainPatchInfo,
        rawResource: OpenJPEG,
        renderContext: RenderContext
    ): GLLoadedTexture {
        Debug.Printf("Terrain: Loading baked texture into GL")
        return GLLoadedTexture(renderContext, rawResource)
    }

    override fun RequestRawResource(params: TerrainPatchInfo, resourceConsumer: ResourceConsumer) {
        terrainTextureCache.RequestResource(params, resourceConsumer)
    }
}
