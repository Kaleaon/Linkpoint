package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.render.terrain.DrawableTerrainPatch
import com.linkpoint.slproto.terrain.TerrainPatchInfo
import java.lang.ref.WeakReference
import javax.annotation.Nonnull

class DrawListTerrainEntry : DrawListEntry() {
    private volatile WeakReference<DrawableTerrainPatch> drawablePatch
    private TerrainPatchInfo patchInfo
    private val Int patchX
    private val Int patchY

    DrawListTerrainEntry(TerrainPatchInfo terrainPatchInfo, Int i, Int i2) {
        this.patchX = i
        this.patchY = i2
        updatePatchInfo(terrainPatchInfo)
    }

    fun addToDrawList(drawList: DrawList) {
        val obj: Object = null
        val weakReference: WeakReference = this.drawablePatch
        if (weakReference != null) {
            obj = (DrawableTerrainPatch) weakReference.get()
        }
        if (obj == null) {
            obj = DrawableTerrainPatch(drawList.drawableStore.terrainGeometryCache, drawList.drawableStore.glTerrainTextureCache, this.patchInfo, this.patchX, this.patchY)
            this.drawablePatch = WeakReference(obj)
        }
        drawList.terrain.add(obj)
    }

    fun updatePatchInfo(terrainPatchInfo: TerrainPatchInfo) {
        this.patchInfo = terrainPatchInfo
        val minHeight: Float = terrainPatchInfo.getMinHeight()
        val maxHeight: Float = terrainPatchInfo.getMaxHeight()
        this.boundingBox[0] = (Float) (this.patchX * 16)
        this.boundingBox[1] = (Float) (this.patchY * 16)
        this.boundingBox[2] = minHeight
        this.boundingBox[3] = (Float) ((this.patchX + 1) * 16)
        this.boundingBox[4] = (Float) ((this.patchY + 1) * 16)
        this.boundingBox[5] = maxHeight
        this.drawablePatch = null
    }
}
