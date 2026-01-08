package com.lumiyaviewer.lumiya.render.glres.textures

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.memory.MemoryManager
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class GLTerrainTextureCache(
    gLLoadQueue: GLLoadQueue,
    private val terrainTextureCache: TerrainTextureCache
) : GLResourceCache<TerrainPatchInfo, OpenJPEG, GLLoadedTexture>(MemoryManager(), gLLoadQueue) {

    override fun CancelRawResource(consumer: ResourceConsumer) {
        terrainTextureCache.CancelRequest(consumer)
    }

    override fun GetResourceSize(raw: OpenJPEG?): Int {
        return raw?.getLoadedSize() ?: 0
    }

    override fun LoadResource(
        params: TerrainPatchInfo,
        raw: OpenJPEG?,
        renderContext: RenderContext
    ): GLLoadedTexture? {
        Debug.Log("Terrain: Loading baked texture into GL")
        return if (raw != null) {
            GLLoadedTexture(renderContext, raw)
        } else {
            null
        }
    }

    override fun RequestRawResource(
        params: TerrainPatchInfo,
        consumer: ResourceConsumer
    ) {
        terrainTextureCache.RequestResource(params, consumer)
    }
}
