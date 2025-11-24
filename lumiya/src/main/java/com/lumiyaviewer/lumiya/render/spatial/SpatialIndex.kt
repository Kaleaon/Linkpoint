package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.lang.ref.WeakReference

object SpatialIndex {
    private var indexHolder: WeakReference<Any>? = null
    private var objectIndex: SpatialObjectIndex? = null

    // Singleton pattern via object in Kotlin is simpler, 
    // but preserving getInstance for compatibility
    fun getInstance(): SpatialIndex {
        return this
    }

    @Synchronized
    fun DisableObjectIndex(obj: Any?) {
        var holderObj: Any? = null
        synchronized(this) {
            val currentHolder = indexHolder
            if (currentHolder != null) {
                holderObj = currentHolder.get()
            }
            
            val currentIdx = objectIndex
            if (currentIdx != null && (holderObj === obj || holderObj == null)) {
                currentIdx.disableIndex()
            }
            indexHolder = null
            objectIndex = null
        }
    }

    @Synchronized
    fun EnableObjectIndex(newIndex: SpatialObjectIndex?, obj: Any?): SpatialObjectIndex? {
        objectIndex = newIndex
        indexHolder = WeakReference(obj)
        return objectIndex
    }

    fun getDrawableAvatar(info: SLObjectInfo?): DrawableAvatar? {
        if (info == null) return null
        return objectIndex?.getDrawableAvatar(info)
    }

    @Synchronized
    fun getObjectIndex(): SpatialObjectIndex? {
        return objectIndex
    }

    fun setAvatarCountLimit(limit: Int) {
        objectIndex?.avatarCountLimit = limit
    }

    fun updateTerrainPatch(x: Int, y: Int, data: TerrainData?) {
        objectIndex?.updateTerrainPatch(x, y, data)
    }
}
