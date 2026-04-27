package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class SpatialIndex {
    private volatile WeakReference<Object> indexHolder;
    private volatile SpatialObjectIndex objectIndex;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InstanceHolder {
        private static final SpatialIndex instance = new SpatialIndex(null);

        private InstanceHolder() {
        }
    }

    private SpatialIndex() {
        this.indexHolder = null;
        this.objectIndex = null;
    }

    /* synthetic */ SpatialIndex(SpatialIndex spatialIndex) {
        this();
    }

    public static SpatialIndex getInstance() {
        return InstanceHolder.instance;
    }

    public synchronized void DisableObjectIndex(Object obj) {
        synchronized (this) {
            SpatialObjectIndex spatialObjectIndex = this.objectIndex;
            Object obj2 = this.indexHolder != null ? this.indexHolder.get() : null;
            if (spatialObjectIndex != null && (obj2 == obj || obj2 == null)) {
                spatialObjectIndex.disableIndex();
            }
            this.indexHolder = null;
            this.objectIndex = null;
        }
    }

    public synchronized SpatialObjectIndex EnableObjectIndex(SpatialObjectIndex spatialObjectIndex, Object obj) {
        this.objectIndex = spatialObjectIndex;
        this.indexHolder = new WeakReference<>(obj);
        return this.objectIndex;
    }

    public DrawableAvatar getDrawableAvatar(SLObjectInfo sLObjectInfo) {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex;
        if (spatialObjectIndex != null) {
            return spatialObjectIndex.getDrawableAvatar(sLObjectInfo);
        }
        return null;
    }

    public synchronized SpatialObjectIndex getObjectIndex() {
        return this.objectIndex;
    }

    public void setAvatarCountLimit(int i) {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex;
        if (spatialObjectIndex != null) {
            spatialObjectIndex.setAvatarCountLimit(i);
        }
    }

    public void updateTerrainPatch(int i, int i2, TerrainData terrainData) {
        SpatialObjectIndex spatialObjectIndex = this.objectIndex;
        if (spatialObjectIndex != null) {
            spatialObjectIndex.updateTerrainPatch(i, i2, terrainData);
        }
    }
}
