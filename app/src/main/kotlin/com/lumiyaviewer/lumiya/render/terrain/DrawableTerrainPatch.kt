package com.lumiyaviewer.lumiya.render.terrain

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class DrawableTerrainPatch(
    terrainGeometryCache: TerrainGeometryCache,
    glTerrainTextureCache: GLTerrainTextureCache?,
    terrainPatchInfo: TerrainPatchInfo,
    x: Int,
    y: Int
) : ResourceConsumer {
    
    private val objWorldMatrix = FloatArray(16)
    
    @Volatile
    private var geometry: TerrainPatchGeometry? = null
    
    @Volatile
    private var texture: GLLoadedTexture? = null

    init {
        Matrix.setIdentityM(objWorldMatrix, 0)
        Matrix.translateM(objWorldMatrix, 0, (x * 16).toFloat(), (y * 16).toFloat(), 0.0f)
        terrainGeometryCache.RequestResource(terrainPatchInfo.heightMap, this)
        glTerrainTextureCache?.RequestResource(terrainPatchInfo, this)
    }

    fun GLDraw(renderContext: RenderContext) {
        geometry?.GLDraw(renderContext, objWorldMatrix, texture)
    }

    override fun OnResourceReady(obj: Any?, z: Boolean) {
        Debug.Printf("DrawableTerrainPatch: got resource = %s", obj?.toString() ?: "null")
        
        when (obj) {
            is TerrainPatchGeometry -> geometry = obj
            is GLLoadedTexture -> texture = obj
        }
    }

    companion object {
        @JvmStatic
        fun GLPrepare(renderContext: RenderContext) {
            TerrainPatchGeometry.GLPrepare(renderContext)
        }
    }
}
