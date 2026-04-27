package com.linkpoint.render.spatial

import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.terrain.TerrainData
import java.lang.ref.WeakReference

class SpatialIndex {
    private volatile WeakReference<Object> indexHolder
    private volatile SpatialObjectIndex objectIndex

    @JvmStatic
private class InstanceHolder {
        private const val SpatialIndex instance = SpatialIndex()

        private InstanceHolder() {
        }
    }

    private SpatialIndex() {
        this.indexHolder = null
        this.objectIndex = null
    }

    /* synthetic */ SpatialIndex(SpatialIndex spatialIndex) {
        this()
    }

    @JvmStatic
     fun getInstance(): SpatialIndex {
        return InstanceHolder.instance
    }

    public synchronized Unit DisableObjectIndex(Object obj) {
        val obj2: Object = null
        synchronized (this) {
            val spatialObjectIndex: SpatialObjectIndex = this.objectIndex
            if (this.indexHolder != null) {
                obj2 = this.indexHolder.get()
            }
            if (spatialObjectIndex != null && (obj2 == obj || obj2 == null)) {
                spatialObjectIndex.disableIndex()
            }
            this.indexHolder = null
            this.objectIndex = null
        }
    }

    public synchronized SpatialObjectIndex EnableObjectIndex(SpatialObjectIndex spatialObjectIndex, Object obj) {
        this.objectIndex = spatialObjectIndex
        this.indexHolder = WeakReference(obj)
        return this.objectIndex
    }

     public fun getDrawableAvatar(sLObjectInfo: SLObjectInfo): DrawableAvatar {
        val spatialObjectIndex: SpatialObjectIndex = this.objectIndex
        return spatialObjectIndex != null ? spatialObjectIndex.getDrawableAvatar(sLObjectInfo) : null
    }

    public synchronized SpatialObjectIndex getObjectIndex() {
        return this.objectIndex
    }

    fun setAvatarCountLimit(i: Int) {
        val spatialObjectIndex: SpatialObjectIndex = this.objectIndex
        if (spatialObjectIndex != null) {
            spatialObjectIndex.setAvatarCountLimit(i)
        }
    }

    fun updateTerrainPatch(i: Int, i2: Int, terrainData: TerrainData) {
        val spatialObjectIndex: SpatialObjectIndex = this.objectIndex
        if (spatialObjectIndex != null) {
            spatialObjectIndex.updateTerrainPatch(i, i2, terrainData)
        }
    }
}
