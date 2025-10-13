package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.lang.ref.WeakReference

class SpatialIndex private constructor() {

    companion object {
        @JvmStatic
        fun getInstance(): SpatialIndex = INSTANCE

        private val INSTANCE = SpatialIndex()
    }

    @Volatile
    private var indexHolder: WeakReference<Any>? = null

    @Volatile
    private var objectIndex: SpatialObjectIndex? = null

    @Synchronized
    fun DisableObjectIndex(holder: Any?) {
        val currentIndex = objectIndex
        val currentHolder = indexHolder?.get()

        if (currentIndex != null && (currentHolder == holder || currentHolder == null)) {
            currentIndex.disableIndex()
        }

        indexHolder = null
        objectIndex = null
    }

    @Synchronized
    fun EnableObjectIndex(newIndex: SpatialObjectIndex, holder: Any): SpatialObjectIndex {
        objectIndex = newIndex
        indexHolder = WeakReference(holder)
        return newIndex
    }

    @Synchronized
    fun getObjectIndex(): SpatialObjectIndex? {
        return objectIndex
    }

    fun getDrawableAvatar(objectInfo: SLObjectInfo): DrawableAvatar? {
        return objectIndex?.getDrawableAvatar(objectInfo)
    }

    fun setAvatarCountLimit(limit: Int) {
        objectIndex?.setAvatarCountLimit(limit)
    }

    fun updateTerrainPatch(x: Int, y: Int, terrainData: TerrainData) {
        objectIndex?.updateTerrainPatch(x, y, terrainData)
    }
}
