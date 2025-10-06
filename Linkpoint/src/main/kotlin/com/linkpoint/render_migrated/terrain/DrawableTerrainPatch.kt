package com.linkpoint.render.terrain
import java.util.*

import android.opengl.Matrix
import com.linkpoint.Debug
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.textures.GLLoadedTexture
import com.linkpoint.render.glres.textures.GLTerrainTextureCache
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.terrain.TerrainGeometryCache
import com.linkpoint.slproto.terrain.TerrainPatchInfo

class DrawableTerrainPatch : ResourceConsumer {
    private volatile TerrainPatchGeometry geometry
    private val Float[] objWorldMatrix = Float[16]
    private volatile GLLoadedTexture texture

    public DrawableTerrainPatch(TerrainGeometryCache terrainGeometryCache, GLTerrainTextureCache gLTerrainTextureCache, TerrainPatchInfo terrainPatchInfo, Int i, Int i2) {
        Matrix.setIdentityM(this.objWorldMatrix, 0)
        Matrix.translateM(this.objWorldMatrix, 0, (Float) (i * 16), (Float) (i2 * 16), 0.0f)
        terrainGeometryCache.RequestResource(terrainPatchInfo.getHeightMap(), this)
        if (gLTerrainTextureCache != null) {
            gLTerrainTextureCache.RequestResource(terrainPatchInfo, this)
        }
    }

    @JvmStatic
    Unit GLPrepare(RenderContext renderContext) {
        TerrainPatchGeometry.GLPrepare(renderContext)
    }

    fun GLDraw(RenderContext renderContext) {
        TerrainPatchGeometry terrainPatchGeometry = this.geometry
        if (terrainPatchGeometry != null) {
            terrainPatchGeometry.GLDraw(renderContext, this.objWorldMatrix, this.texture)
        }
    }

    fun OnResourceReady(Object obj, Boolean z) {
        Object[] objArr = Object[1]
        objArr[0] = obj != null ? obj.toString() : "null"
        Debug.Printf("DrawableTerrainPatch: got resource = %s", objArr)
        if (obj instanceof TerrainPatchGeometry) {
            this.geometry = (TerrainPatchGeometry) obj
        } else if (obj instanceof GLLoadedTexture) {
            this.texture = (GLLoadedTexture) obj
        }
    }
}
