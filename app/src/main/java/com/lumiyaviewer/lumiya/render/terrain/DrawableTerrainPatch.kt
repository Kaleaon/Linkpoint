package com.lumiyaviewer.lumiya.render.terrain
import java.util.*

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo

class DrawableTerrainPatch : ResourceConsumer {
    private volatile TerrainPatchGeometry geometry
    private Float[] objWorldMatrix = Float[16]
    private volatile GLLoadedTexture texture

    constructor(terrainGeometryCache: TerrainGeometryCache, gLTerrainTextureCache: GLTerrainTextureCache, terrainPatchInfo: TerrainPatchInfo, i: Int, i2: Int) {
        Matrix.setIdentityM(this.objWorldMatrix, 0)
        Matrix.translateM(this.objWorldMatrix, 0, (Float) (i * 16), (Float) (i2 * 16), 0.0f)
        terrainGeometryCache.RequestResource(terrainPatchInfo.getHeightMap(), this)
        gLTerrainTextureCache?.RequestResource(terrainPatchInfo, this)
        }
    }

    fun GLPrepare(renderContext: RenderContext): Unit {
        TerrainPatchGeometry.GLPrepare(renderContext)
    }

    fun GLDraw(renderContext: RenderContext): Unit {
        TerrainPatchGeometry terrainPatchGeometry = this.geometry
        terrainPatchGeometry?.GLDraw(renderContext, this.objWorldMatrix, this.texture)
        }
    }

    fun OnResourceReady(obj: Any, z: Boolean): Unit {
        Any[] objArr = Any[1]
        objArr[0] = obj != null ? obj.toString() : "null"
        Debug.Printf("DrawableTerrainPatch: got resource = %s", objArr)
        if (obj instanceof TerrainPatchGeometry) {
            this.geometry = (TerrainPatchGeometry) obj
        } else if (obj instanceof GLLoadedTexture) {
            this.texture = (GLLoadedTexture) obj
        }
    }
}
