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

public class SpatialObjectIndex {
    private static final int NUM_DEPTH_BINS = 16;
    private static final float REGION_SIZE_XY = 256.0f;
    private static final float REGION_SIZE_Z = 4096.0f;
    /* access modifiers changed from: private */
    public volatile int avatarCountLimit = 5;
    /* access modifiers changed from: private */
    public final AtomicBoolean drawListUpdateRequested = new AtomicBoolean(false);
    private final DrawListUpdateTask drawListUpdateTask = new DrawListUpdateTask(this, (DrawListUpdateTask) null);
    private final DrawableStore drawableStore;
    /* access modifiers changed from: private */
    public final AtomicBoolean frustrumChanged = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public volatile FrustrumInfo frustrumInfo = null;
    /* access modifiers changed from: private */
    public volatile FrustrumPlanes frustrumPlanes = null;
    /* access modifiers changed from: private */
    public volatile boolean indexDisabled = false;
    /* access modifiers changed from: private */
    public volatile boolean initialUpdateCompleted = false;
    private final Object lock = new Object();
    /* access modifiers changed from: private */
    public final Object objectUpdateRemoveLock = new Object();
    /* access modifiers changed from: private */
    public volatile DrawList objectsInFrustrum;
    /* access modifiers changed from: private */
    public final Set<DrawListObjectEntry> objectsToRemove = Collections.newSetFromMap(new IdentityHashMap());
    /* access modifiers changed from: private */
    public final Set<DrawListObjectEntry> objectsToUpdate = Collections.newSetFromMap(new IdentityHashMap());
    private final ObjectsUpdateTask objectsUpdateTask = new ObjectsUpdateTask(this, (ObjectsUpdateTask) null);
    /* access modifiers changed from: private */
    public final SpatialTree spatialTree;
    /* access modifiers changed from: private */
    public final DrawListTerrainEntry[][] terrain = ((DrawListTerrainEntry[][]) Array.newInstance(DrawListTerrainEntry.class, new int[]{16, 16}));
    /* access modifiers changed from: private */
    public final Map<Integer, TerrainData> terrainDirty = new HashMap();
    /* access modifiers changed from: private */
    public final Object terrainLock = new Object();
    private final Runnable terrainUpdate = new Runnable() {
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

    private class DrawListUpdateTask implements Runnable, WeakQueue.LowPriority {
        private DrawListUpdateTask() {
        }

        /* synthetic */ DrawListUpdateTask(SpatialObjectIndex spatialObjectIndex, DrawListUpdateTask drawListUpdateTask) {
            this();
        }

        public void run() {
            if (SpatialObjectIndex.this.initialUpdateCompleted && (!SpatialObjectIndex.this.indexDisabled)) {
                if (!SpatialObjectIndex.this.frustrumChanged.getAndSet(false) ? SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded() : true) {
                    FrustrumPlanes r0 = SpatialObjectIndex.this.frustrumPlanes;
                    FrustrumInfo r1 = SpatialObjectIndex.this.frustrumInfo;
                    if (!(r0 == null || r1 == null)) {
                        SpatialObjectIndex.this.spatialTree.walkTree(r0, r1.viewDistance);
                    }
                }
                if (SpatialObjectIndex.this.spatialTree.isDrawListChanged()) {
                    DrawList unused = SpatialObjectIndex.this.objectsInFrustrum = SpatialObjectIndex.this.getObjectsInCells(SpatialObjectIndex.this.avatarCountLimit);
                }
            }
        }
    }

    private class ObjectsUpdateTask implements Runnable {
        private ObjectsUpdateTask() {
        }

        /* synthetic */ ObjectsUpdateTask(SpatialObjectIndex spatialObjectIndex, ObjectsUpdateTask objectsUpdateTask) {
            this();
        }

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
                for (DrawListObjectEntry r3 : drawListObjectEntryArr2) {
                    z |= SpatialObjectIndex.this.handleRemoveObject(r3);
                }
                if (z || SpatialObjectIndex.this.spatialTree.isDrawListChanged() || SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded()) {
                    SpatialObjectIndex.this.drawListUpdateRequested.set(true);
                }
            }
        }
    }

    public SpatialObjectIndex(DrawableStore drawableStore2, int i) {
        this.drawableStore = drawableStore2;
        this.avatarCountLimit = i;
        this.spatialTree = new SpatialTree(16, REGION_SIZE_XY, REGION_SIZE_XY, REGION_SIZE_Z, this);
        this.objectsInFrustrum = DrawList.create(drawableStore2, (DrawList) null, i);
    }

    /* access modifiers changed from: private */
    public DrawList getObjectsInCells(int i) {
        DrawList create = DrawList.create(this.drawableStore, this.objectsInFrustrum, i);
        this.spatialTree.addDrawables(create);
        create.initRenderPasses();
        return create;
    }

    /* access modifiers changed from: private */
    public boolean handleRemoveObject(DrawListObjectEntry drawListObjectEntry) {
        this.spatialTree.removeObject(drawListObjectEntry);
        drawListObjectEntry.getObjectInfo().clearDrawListEntry();
        return false;
    }

    /* access modifiers changed from: private */
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
        if (!this.indexDisabled) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask);
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate);
            this.drawListUpdateRequested.set(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void disableIndex() {
        this.indexDisabled = true;
    }

    /* access modifiers changed from: package-private */
    public DrawableAvatar getDrawableAvatar(SLObjectInfo sLObjectInfo) {
        return this.drawableStore.drawableAvatarCache.getIfPresent(sLObjectInfo);
    }

    public DrawList getObjectsInFrustrum() {
        return this.objectsInFrustrum;
    }

    /* access modifiers changed from: package-private */
    public void requestEntryRemoval(DrawListEntry drawListEntry) {
        if (drawListEntry instanceof DrawListObjectEntry) {
            removeObject((DrawListObjectEntry) drawListEntry);
        }
    }

    public void setAvatarCountLimit(int i) {
        this.avatarCountLimit = i;
    }

    public void setViewport(FrustrumInfo frustrumInfo2, FrustrumPlanes frustrumPlanes2) {
        boolean z = true;
        synchronized (this.lock) {
            if (this.frustrumInfo == null) {
                this.frustrumInfo = frustrumInfo2;
            } else if (!this.frustrumInfo.equals(frustrumInfo2)) {
                this.frustrumInfo = frustrumInfo2;
            } else {
                z = false;
            }
            if (z) {
                this.frustrumPlanes = frustrumPlanes2;
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

    /* access modifiers changed from: package-private */
    public void updateTerrainPatch(int i, int i2, TerrainData terrainData) {
        synchronized (this.terrainLock) {
            this.terrainDirty.put(Integer.valueOf((i2 * 16) + i), terrainData);
        }
        if (this.initialUpdateCompleted && (!this.indexDisabled)) {
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate);
        }
    }
}
