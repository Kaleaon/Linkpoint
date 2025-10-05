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
    SpatialIndex getInstance() {
        return InstanceHolder.instance
    }

    public synchronized Unit DisableObjectIndex(Object obj) {
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

    public synchronized SpatialObjectIndex EnableObjectIndex(SpatialObjectIndex spatialObjectIndex, Object obj) {
        this.objectIndex = spatialObjectIndex
        this.indexHolder = WeakReference(obj)
        return this.objectIndex
    }

    public DrawableAvatar getDrawableAvatar(SLObjectInfo sLObjectInfo) {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex
        return spatialObjectIndex != null ? spatialObjectIndex.getDrawableAvatar(sLObjectInfo) : null
    }

    public synchronized SpatialObjectIndex getObjectIndex() {
        return this.objectIndex
    }

    public Unit setAvatarCountLimit(Int i) {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex
        if (spatialObjectIndex != null) {
            spatialObjectIndex.setAvatarCountLimit(i)
        }
    }

    public Unit updateTerrainPatch(Int i, Int i2, TerrainData terrainData) {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex
        if (spatialObjectIndex != null) {
            spatialObjectIndex.updateTerrainPatch(i, i2, terrainData)
        }
    }
}
