package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo
import java.lang.ref.WeakReference

internal class DrawListTerrainEntry(
    patchInfo: TerrainPatchInfo,
    private val patchX: Int,
    private val patchY: Int
) : DrawListEntry() {

    private var patchInfo: TerrainPatchInfo = patchInfo
        private set

    @Volatile
    private var drawablePatch: WeakReference<DrawableTerrainPatch>? = null

    init {
        updatePatchInfo(patchInfo)
    }

    override fun addToDrawList(drawList: DrawList) {
        var patch = drawablePatch?.get()
        if (patch == null) {
            patch = DrawableTerrainPatch(
                drawList.drawableStore.terrainGeometryCache,
                drawList.drawableStore.glTerrainTextureCache,
                patchInfo,
                patchX,
                patchY
            )
            drawablePatch = WeakReference(patch)
        }
        drawList.terrain.add(patch)
    }

    fun updatePatchInfo(newPatchInfo: TerrainPatchInfo) {
        patchInfo = newPatchInfo
        
        val minHeight = newPatchInfo.minHeight
        val maxHeight = newPatchInfo.maxHeight
        
        boundingBox[0] = (patchX * 16).toFloat()
        boundingBox[1] = (patchY * 16).toFloat()
        boundingBox[2] = minHeight
        boundingBox[3] = ((patchX + 1) * 16).toFloat()
        boundingBox[4] = ((patchY + 1) * 16).toFloat()
        boundingBox[5] = maxHeight
        
        drawablePatch = null
    }
}
