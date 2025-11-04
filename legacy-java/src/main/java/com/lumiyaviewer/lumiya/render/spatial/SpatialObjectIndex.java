package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class SpatialObjectIndex {
    private static final int NUM_DEPTH_BINS = 16;
    private static final float REGION_SIZE_XY = 256.0f;
    private static final float REGION_SIZE_Z = 4096.0f;
    private volatile int avatarCountLimit;
    private final DrawableStore drawableStore;
    private volatile DrawList objectsInFrustrum;
    private final AtomicBoolean frustrumChanged = new AtomicBoolean(false);
    private final AtomicBoolean drawListUpdateRequested = new AtomicBoolean(false);
    private final Object objectUpdateRemoveLock = new Object();
    private final Set<DrawListObjectEntry> objectsToUpdate = Collections.newSetFromMap(new IdentityHashMap());
    private final Set<DrawListObjectEntry> objectsToRemove = Collections.newSetFromMap(new IdentityHashMap());
    private volatile boolean initialUpdateCompleted = false;
    private volatile boolean indexDisabled = false;
    private final Object terrainLock = new Object();
    private final DrawListTerrainEntry[][] terrain = (DrawListTerrainEntry[][]) Array.newInstance((Class<?>) DrawListTerrainEntry.class, 16, 16);
    private final Map<Integer, TerrainData> terrainDirty = new HashMap();
    private final Object lock = new Object();
    private volatile FrustrumInfo frustrumInfo = null;
    private volatile FrustrumPlanes frustrumPlanes = null;
    private final DrawListUpdateTask drawListUpdateTask = new DrawListUpdateTask(this, null);
    private final ObjectsUpdateTask objectsUpdateTask = new ObjectsUpdateTask(this, 0 == true ? 1 : 0);
    private final Runnable terrainUpdate = new Runnable() { // from class: com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex.1
        @Override // java.lang.Runnable
        public void run() {
            TerrainData terrainData;
            int i;
            boolean z;
            boolean z2;
            DrawListTerrainEntry drawListTerrainEntry;
            DrawListTerrainEntry drawListTerrainEntry2;
            boolean z3 = false;
            while (SpatialObjectIndex.this.initialUpdateCompleted && (!SpatialObjectIndex.this.indexDisabled)) {
                synchronized (SpatialObjectIndex.this.terrainLock) {
                    Iterator it = SpatialObjectIndex.this.terrainDirty.entrySet().iterator();
                    if (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        int intValue = ((Integer) entry.getKey()).intValue();
                        TerrainData terrainData2 = (TerrainData) entry.getValue();
                        it.remove();
                        i = intValue;
                        terrainData = terrainData2;
                        z = true;
                    } else {
                        terrainData = null;
                        i = -1;
                        z = false;
                    }
                }
                if (!z) {
                    break;
                }
                if (i < 0 || terrainData == null) {
                    z2 = z3;
                } else {
                    int i2 = i % 16;
                    int i3 = i / 16;
                    TerrainPatchInfo patchInfo = terrainData.getPatchInfo(i2, i3);
                    if (patchInfo != null) {
                        synchronized (SpatialObjectIndex.this.terrainLock) {
                            drawListTerrainEntry2 = SpatialObjectIndex.this.terrain[i2][i3];
                            if (drawListTerrainEntry2 == null) {
                                DrawListTerrainEntry[] drawListTerrainEntryArr = SpatialObjectIndex.this.terrain[i2];
                                drawListTerrainEntry2 = new DrawListTerrainEntry(patchInfo, i2, i3);
                                drawListTerrainEntryArr[i3] = drawListTerrainEntry2;
                            } else {
                                drawListTerrainEntry2.updatePatchInfo(patchInfo);
                            }
                        }
                        SpatialObjectIndex.this.spatialTree.updateObject(drawListTerrainEntry2);
                    } else {
                        synchronized (SpatialObjectIndex.this.terrainLock) {
                            drawListTerrainEntry = SpatialObjectIndex.this.terrain[i2][i3];
                            SpatialObjectIndex.this.terrain[i2][i3] = null;
                        }
                        if (drawListTerrainEntry != null) {
                            SpatialObjectIndex.this.spatialTree.removeObject(drawListTerrainEntry);
                        }
                    }
                    z2 = true;
                }
                z3 = z2;
            }
            if (z3) {
                SpatialObjectIndex.this.drawListUpdateRequested.set(true);
            }
        }
    };
    private final SpatialTree spatialTree = new SpatialTree(16, REGION_SIZE_XY, REGION_SIZE_XY, REGION_SIZE_Z, this);

    /* loaded from: classes.dex */
    private class DrawListUpdateTask implements Runnable, WeakQueue.LowPriority {
        private DrawListUpdateTask() {
        }

        /* synthetic */ DrawListUpdateTask(SpatialObjectIndex spatialObjectIndex, DrawListUpdateTask drawListUpdateTask) {
            this();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SpatialObjectIndex.this.initialUpdateCompleted && (!SpatialObjectIndex.this.indexDisabled)) {
                if (!SpatialObjectIndex.this.frustrumChanged.getAndSet(false) ? SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded() : true) {
                    FrustrumPlanes frustrumPlanes = SpatialObjectIndex.this.frustrumPlanes;
                    FrustrumInfo frustrumInfo = SpatialObjectIndex.this.frustrumInfo;
                    if (frustrumPlanes != null && frustrumInfo != null) {
                        SpatialObjectIndex.this.spatialTree.walkTree(frustrumPlanes, frustrumInfo.viewDistance);
                    }
                }
                if (SpatialObjectIndex.this.spatialTree.isDrawListChanged()) {
                    SpatialObjectIndex.this.objectsInFrustrum = SpatialObjectIndex.this.getObjectsInCells(SpatialObjectIndex.this.avatarCountLimit);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ObjectsUpdateTask implements Runnable {
        private ObjectsUpdateTask() {
        }

        /* synthetic */ ObjectsUpdateTask(SpatialObjectIndex spatialObjectIndex, ObjectsUpdateTask objectsUpdateTask) {
            this();
        }

        @Override // java.lang.Runnable
        public void run() {
            DrawListObjectEntry[] drawListObjectEntryArr;
            DrawListObjectEntry[] drawListObjectEntryArr2;
            if (SpatialObjectIndex.this.initialUpdateCompleted && (!SpatialObjectIndex.this.indexDisabled)) {
                synchronized (SpatialObjectIndex.this.objectUpdateRemoveLock) {
                    drawListObjectEntryArr = (DrawListObjectEntry[]) SpatialObjectIndex.this.objectsToUpdate.toArray(new DrawListObjectEntry[SpatialObjectIndex.this.objectsToUpdate.size()]);
                    drawListObjectEntryArr2 = (DrawListObjectEntry[]) SpatialObjectIndex.this.objectsToRemove.toArray(new DrawListObjectEntry[SpatialObjectIndex.this.objectsToRemove.size()]);
                    SpatialObjectIndex.this.objectsToUpdate.clear();
                    SpatialObjectIndex.this.objectsToRemove.clear();
                }
                boolean z = false;
                for (DrawListObjectEntry drawListObjectEntry : drawListObjectEntryArr) {
                    z |= !drawListObjectEntry.getObjectInfo().isDead ? SpatialObjectIndex.this.handleUpdateObject(drawListObjectEntry) : SpatialObjectIndex.this.handleRemoveObject(drawListObjectEntry);
                }
                for (DrawListObjectEntry drawListObjectEntry2 : drawListObjectEntryArr2) {
                    z |= SpatialObjectIndex.this.handleRemoveObject(drawListObjectEntry2);
                }
                if (z || SpatialObjectIndex.this.spatialTree.isDrawListChanged() || SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded()) {
                    SpatialObjectIndex.this.drawListUpdateRequested.set(true);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public SpatialObjectIndex(DrawableStore drawableStore, int i) {
        this.avatarCountLimit = 5;
        this.drawableStore = drawableStore;
        this.avatarCountLimit = i;
        this.objectsInFrustrum = DrawList.create(drawableStore, null, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DrawList getObjectsInCells(int i) {
        DrawList create = DrawList.create(this.drawableStore, this.objectsInFrustrum, i);
        this.spatialTree.addDrawables(create);
        create.initRenderPasses();
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleRemoveObject(DrawListObjectEntry drawListObjectEntry) {
        this.spatialTree.removeObject(drawListObjectEntry);
        drawListObjectEntry.getObjectInfo().clearDrawListEntry();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleUpdateObject(DrawListObjectEntry drawListObjectEntry) {
        drawListObjectEntry.updateBoundingBox();
        this.spatialTree.updateObject(drawListObjectEntry);
        return false;
    }

    private void removeObject(DrawListObjectEntry drawListObjectEntry) {
        boolean remove;
        synchronized (this.objectUpdateRemoveLock) {
            remove = this.objectsToUpdate.remove(drawListObjectEntry) | this.objectsToRemove.add(drawListObjectEntry);
        }
        if (remove && this.initialUpdateCompleted && (!this.indexDisabled)) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
        }
    }

    public void completeInitialUpdate() {
        this.initialUpdateCompleted = true;
        if (this.indexDisabled) {
            return;
        }
        PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
        PrimComputeExecutor.getInstance().execute(this.terrainUpdate);
        this.drawListUpdateRequested.set(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableIndex() {
        this.indexDisabled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableAvatar getDrawableAvatar(SLObjectInfo sLObjectInfo) {
        return this.drawableStore.drawableAvatarCache.getIfPresent(sLObjectInfo);
    }

    public DrawList getObjectsInFrustrum() {
        return this.objectsInFrustrum;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        if (drawListEntry instanceof DrawListObjectEntry) {
            removeObject((DrawListObjectEntry) drawListEntry);
        }
    }

    public void setAvatarCountLimit(int i) {
        this.avatarCountLimit = i;
    }

    public void setViewport(FrustrumInfo frustrumInfo, FrustrumPlanes frustrumPlanes) {
        boolean z = true;
        synchronized (this.lock) {
            if (this.frustrumInfo == null) {
                this.frustrumInfo = frustrumInfo;
            } else if (this.frustrumInfo.equals(frustrumInfo)) {
                z = false;
            } else {
                this.frustrumInfo = frustrumInfo;
            }
            if (z) {
                this.frustrumPlanes = frustrumPlanes;
                this.frustrumChanged.set(true);
                if (this.initialUpdateCompleted && (!this.indexDisabled)) {
                    this.drawListUpdateRequested.set(true);
                }
            }
        }
    }

    public boolean updateDrawListIfNeeded() {
        if (!this.drawListUpdateRequested.getAndSet(false)) {
            return false;
        }
        PrimComputeExecutor.getInstance().execute(this.drawListUpdateTask);
        return true;
    }

    public void updateObject(DrawListObjectEntry drawListObjectEntry) {
        boolean add;
        synchronized (this.objectUpdateRemoveLock) {
            add = !drawListObjectEntry.getObjectInfo().isDead ? this.objectsToUpdate.add(drawListObjectEntry) : this.objectsToRemove.add(drawListObjectEntry);
        }
        if (add && this.initialUpdateCompleted && (!this.indexDisabled)) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateTerrainPatch(int i, int i2, TerrainData terrainData) {
        synchronized (this.terrainLock) {
            this.terrainDirty.put(Integer.valueOf((i2 * 16) + i), terrainData);
        }
        if (this.initialUpdateCompleted && (!this.indexDisabled)) {
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate);
        }
    }
}
