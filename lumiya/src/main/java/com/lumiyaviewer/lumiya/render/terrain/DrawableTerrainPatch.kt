package com.lumiyaviewer.lumiya.render.terrain

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.GLCleanable
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class DrawableTerrainPatch(
    private val geometryCache: TerrainGeometryCache,
    private val textureCache: GLTerrainTextureCache,
    private val patchInfo: TerrainPatchInfo,
    private val x: Int,
    private val y: Int
) : ResourceConsumer, GLCleanable {

    private var terrainTexture: GLLoadedTexture? = null
    private var textureRequested: Boolean = false

    override fun GLCleanup() {
        textureCache.CancelRequest(this)
        terrainTexture = null
        textureRequested = false
    }

    fun GLDraw(renderContext: RenderContext) {
        // Stub
    }

    override fun OnResourceReady(resource: Any?, isIntermediate: Boolean) {
        if (resource is GLLoadedTexture) {
            terrainTexture = resource
        }
    }
    
    init {
        textureCache.RequestResource(patchInfo, this)
    }
}
