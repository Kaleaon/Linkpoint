package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo
import java.lang.ref.WeakReference

class DrawListTerrainEntry(
    terrainPatchInfo: TerrainPatchInfo,
    private val patchX: Int,
    private val patchY: Int
) : DrawListEntry() {

    private var drawablePatch: WeakReference<DrawableTerrainPatch>? = null
    private var patchInfo: TerrainPatchInfo? = null

    init {
        updatePatchInfo(terrainPatchInfo)
    }

    override fun addToDrawList(drawList: DrawList) {
        var obj: DrawableTerrainPatch? = null
        val weakReference = this.drawablePatch
        if (weakReference != null) {
            obj = weakReference.get()
        }
        if (obj == null) {
            obj = DrawableTerrainPatch(
                drawList.drawableStore.terrainGeometryCache,
                drawList.drawableStore.glTerrainTextureCache,
                this.patchInfo!!,
                this.patchX,
                this.patchY
            )
            this.drawablePatch = WeakReference(obj)
        }
        drawList.terrain.add(obj!!)
    }

    fun updatePatchInfo(terrainPatchInfo: TerrainPatchInfo) {
        this.patchInfo = terrainPatchInfo
        val minHeight = terrainPatchInfo.getMinHeight()
        val maxHeight = terrainPatchInfo.getMaxHeight()
        this.boundingBox[0] = (this.patchX * 16).toFloat()
        this.boundingBox[1] = (this.patchY * 16).toFloat()
        this.boundingBox[2] = minHeight
        this.boundingBox[3] = ((this.patchX + 1) * 16).toFloat()
        this.boundingBox[4] = ((this.patchY + 1) * 16).toFloat()
        this.boundingBox[5] = maxHeight
        this.drawablePatch = null
    }
}
