// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.lang.ref.WeakReference;

public class SpatialIndex
{
    private volatile WeakReference<Object> indexHolder;
    private volatile SpatialObjectIndex objectIndex;
    
    private SpatialIndex() {
        this.indexHolder = null;
        this.objectIndex = null;
    }
    
    public static SpatialIndex getInstance() {
        return InstanceHolder.instance;
    }
    
    public void DisableObjectIndex(final Object o) {
        Object value = null;
        synchronized (this) {
            final SpatialObjectIndex objectIndex = this.objectIndex;
            if (this.indexHolder != null) {
                value = this.indexHolder.get();
            }
            if (objectIndex != null && (value == o || value == null)) {
                objectIndex.disableIndex();
            }
            this.indexHolder = null;
            this.objectIndex = null;
        }
    }
    
    public SpatialObjectIndex EnableObjectIndex(SpatialObjectIndex objectIndex, final Object referent) {
        synchronized (this) {
            this.objectIndex = objectIndex;
            this.indexHolder = new WeakReference<Object>(referent);
            objectIndex = this.objectIndex;
            return objectIndex;
        }
    }
    
    public DrawableAvatar getDrawableAvatar(final SLObjectInfo slObjectInfo) {
        final SpatialObjectIndex objectIndex = this.objectIndex;
        if (objectIndex != null) {
            return objectIndex.getDrawableAvatar(slObjectInfo);
        }
        return null;
    }
    
    public SpatialObjectIndex getObjectIndex() {
        synchronized (this) {
            return this.objectIndex;
        }
    }
    
    public void setAvatarCountLimit(final int avatarCountLimit) {
        final SpatialObjectIndex objectIndex = this.objectIndex;
        if (objectIndex != null) {
            objectIndex.setAvatarCountLimit(avatarCountLimit);
        }
    }
    
    public void updateTerrainPatch(final int n, final int n2, final TerrainData terrainData) {
        final SpatialObjectIndex objectIndex = this.objectIndex;
        if (objectIndex != null) {
            objectIndex.updateTerrainPatch(n, n2, terrainData);
        }
    }
    
    private static class InstanceHolder
    {
        private static final SpatialIndex instance;
        
        static {
            instance = new SpatialIndex(null);
        }
    }
}
