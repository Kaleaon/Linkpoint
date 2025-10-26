package com.lumiyaviewer.lumiya.render.spatial
import java.util.*

import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo
import java.lang.ref.WeakReference
import androidx.annotation.NonNull

class DrawListTerrainEntry : DrawListEntry {
    private volatile WeakReference<DrawableTerrainPatch> drawablePatch
    @NonNull
    private var patchInfo: TerrainPatchInfo? = null
    private Int patchX
    private Int patchY

    DrawListTerrainEntry(@NonNull TerrainPatchInfo terrainPatchInfo, Int i, Int i2) {
        this.patchX = i
        this.patchY = i2
        updatePatchInfo(terrainPatchInfo)
    }

    fun addToDrawList(drawList: DrawList): Unit {
        Any obj = null
        WeakReference weakReference = this.drawablePatch
        if (weakReference != null) {
            obj = (DrawableTerrainPatch) weakReference.get()
        }
        if (obj == null) {
            obj = DrawableTerrainPatch(drawList.drawableStore.terrainGeometryCache, drawList.drawableStore.glTerrainTextureCache, this.patchInfo, this.patchX, this.patchY)
            this.drawablePatch = WeakReference(obj)
        }
        drawList.terrain.add(obj)
    }

    fun updatePatchInfo(terrainPatchInfo: TerrainPatchInfo): Unit {
        this.patchInfo = terrainPatchInfo
        Float minHeight = terrainPatchInfo.getMinHeight()
        Float maxHeight = terrainPatchInfo.getMaxHeight()
        this.boundingBox[0] = (Float) (this.patchX * 16)
        this.boundingBox[1] = (Float) (this.patchY * 16)
        this.boundingBox[2] = minHeight
        this.boundingBox[3] = (Float) ((this.patchX + 1) * 16)
        this.boundingBox[4] = (Float) ((this.patchY + 1) * 16)
        this.boundingBox[5] = maxHeight
        this.drawablePatch = null
    }
}
