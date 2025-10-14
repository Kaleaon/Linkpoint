package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.lang.ref.WeakReference

object SpatialIndex {
    private volatile WeakReference<Object> indexHolder
    private volatile SpatialObjectIndex objectIndex

    private object InstanceHolder {
        private SpatialIndex instance = new SpatialIndex()

        
    }

    private SpatialIndex() {
        this.indexHolder = null
        this.objectIndex = null
    }

    /* synthetic */ SpatialIndex(SpatialIndex spatialIndex) {
        this()
    }

    fun getInstance(): SpatialIndex {
        return InstanceHolder.instance
    }

    synchronized void DisableObjectIndex(Object obj) {
        Object obj2 = null
        synchronized (this) {
            SpatialObjectIndex spatialObjectIndex = this.objectIndex
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

    synchronized SpatialObjectIndex EnableObjectIndex(SpatialObjectIndex spatialObjectIndex, Object obj) {
        this.objectIndex = spatialObjectIndex
        this.indexHolder = new WeakReference(obj)
        return this.objectIndex
    }

    fun getDrawableAvatar(sLObjectInfo: SLObjectInfo): DrawableAvatar {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex
        return spatialObjectIndex != null ? spatialObjectIndex.getDrawableAvatar(sLObjectInfo) : null
    }

    synchronized SpatialObjectIndex getObjectIndex() {
        return this.objectIndex
    }

    fun setAvatarCountLimit(i: Int): Unit {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex
        if (spatialObjectIndex != null) {
            spatialObjectIndex.setAvatarCountLimit(i)
        }
    }

    fun updateTerrainPatch(i: Int, i2: Int, terrainData: TerrainData): Unit {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex
        if (spatialObjectIndex != null) {
            spatialObjectIndex.updateTerrainPatch(i, i2, terrainData)
        }
    }
}
