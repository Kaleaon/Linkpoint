package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.res.collections.WeakQueue.LowPriority
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo
import java.lang.reflect.Array
import java.util.Collections
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.Iterator
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import java.util.concurrent.atomic.AtomicBoolean

class SpatialObjectIndex {
    private Int NUM_DEPTH_BINS = 16
    private Float REGION_SIZE_XY = 256.0f
    private Float REGION_SIZE_Z = 4096.0f
    private volatile Int avatarCountLimit = 5
    private val drawListUpdateRequested: AtomicBoolean = AtomicBoolean(false)
    private val drawListUpdateTask: DrawListUpdateTask = DrawListUpdateTask(this, null)
    private DrawableStore drawableStore
    private val frustrumChanged: AtomicBoolean = AtomicBoolean(false)
    private volatile FrustrumInfo frustrumInfo = null
    private volatile FrustrumPlanes frustrumPlanes = null
    private volatile Boolean indexDisabled = false
    private volatile Boolean initialUpdateCompleted = false
    private val lock: Any = Any()
    private val objectUpdateRemoveLock: Any = Any()
    private volatile DrawList objectsInFrustrum
    private val objectsToRemove: Set<DrawListObjectEntry> = Collections.newSetFromMap(IdentityHashMap())
    private val objectsToUpdate: Set<DrawListObjectEntry> = Collections.newSetFromMap(IdentityHashMap())
    private val objectsUpdateTask: ObjectsUpdateTask = ObjectsUpdateTask(this, null)
    private SpatialTree spatialTree
    private DrawListTerrainEntry[][] terrain = ((DrawListTerrainEntry[][]) Array.newInstance(DrawListTerrainEntry.class, Int[]{16, 16}))
    private val terrainDirty: Map<Int, TerrainData> = HashMap()
    private val terrainLock: Any = Any()
    private val terrainUpdate: Runnable = Runnable() {
        fun run(): Unit {
            Boolean z = false
            while (SpatialObjectIndex.this.initialUpdateCompleted && (SpatialObjectIndex.this.indexDisabled ^ 1) != 0) {
                TerrainData terrainData
                synchronized (SpatialObjectIndex.this.terrainLock) {
                    Iterator it = SpatialObjectIndex.this.terrainDirty.entrySet().iterator()
                    if (it.hasNext()) {
                        Entry entry = (Entry) it.next()
                        Int intValue = ((Int) entry.getKey()).intValue()
                        TerrainData terrainData2 = (TerrainData) entry.getValue()
                        it.remove()
                        i = intValue
                        terrainData = terrainData2
                        z2 = true
                    } else {
                        terrainData = null
                        i = -1
                        z2 = false
                    }
                }
                if (!z2) {
                    break
                }
                if (i < 0 || terrainData == null) {
                    z2 = z
                } else {
                    Int i2 = i % 16
                    i /= 16
                    TerrainPatchInfo patchInfo = terrainData.getPatchInfo(i2, i)
                    DrawListEntry drawListEntry
                    if (patchInfo != null) {
                        synchronized (SpatialObjectIndex.this.terrainLock) {
                            drawListEntry = SpatialObjectIndex.this.terrain[i2][i]
                            if (drawListEntry == null) {
                                DrawListTerrainEntry[] drawListTerrainEntryArr = SpatialObjectIndex.this.terrain[i2]
                                drawListEntry = DrawListTerrainEntry(patchInfo, i2, i)
                                drawListTerrainEntryArr[i] = drawListEntry
                            } else {
                                drawListEntry.updatePatchInfo(patchInfo)
                            }
                        }
                        SpatialObjectIndex.this.spatialTree.updateObject(drawListEntry)
                    } else {
                        synchronized (SpatialObjectIndex.this.terrainLock) {
                            drawListEntry = SpatialObjectIndex.this.terrain[i2][i]
                            SpatialObjectIndex.this.terrain[i2][i] = null
                        }
                        if (drawListEntry != null) {
                            SpatialObjectIndex.this.spatialTree.removeObject(drawListEntry)
                        }
                    }
                    z2 = true
                }
                z = z2
            }
            if (z) {
                SpatialObjectIndex.this.drawListUpdateRequested.set(true)
            }
        }
    }

    private class DrawListUpdateTask : Runnable, LowPriority {
        private DrawListUpdateTask() {
        }

        /* synthetic */ DrawListUpdateTask(SpatialObjectIndex spatialObjectIndex, DrawListUpdateTask drawListUpdateTask) {
            this()
        }

        fun run(): Unit {
            if (SpatialObjectIndex.this.initialUpdateCompleted && (SpatialObjectIndex.this.indexDisabled ^ 1) != 0) {
                if (!SpatialObjectIndex.this.frustrumChanged.getAndSet(false) ? SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded() : true) {
                    FrustrumPlanes -get4 = SpatialObjectIndex.this.frustrumPlanes
                    FrustrumInfo -get3 = SpatialObjectIndex.this.frustrumInfo
                    if (!(-get4 == null || -get3 == null)) {
                        SpatialObjectIndex.this.spatialTree.walkTree(-get4, -get3.viewDistance)
                    }
                }
                if (SpatialObjectIndex.this.spatialTree.isDrawListChanged()) {
                    SpatialObjectIndex.this.objectsInFrustrum = SpatialObjectIndex.this.getObjectsInCells(SpatialObjectIndex.this.avatarCountLimit)
                }
            }
        }
    }

    private class ObjectsUpdateTask : Runnable {
        private ObjectsUpdateTask() {
        }

        /* synthetic */ ObjectsUpdateTask(SpatialObjectIndex spatialObjectIndex, ObjectsUpdateTask objectsUpdateTask) {
            this()
        }

        fun run(): Unit {
            if (SpatialObjectIndex.this.initialUpdateCompleted && (SpatialObjectIndex.this.indexDisabled ^ 1) != 0) {
                DrawListObjectEntry[] drawListObjectEntryArr
                synchronized (SpatialObjectIndex.this.objectUpdateRemoveLock) {
                    DrawListObjectEntry[] drawListObjectEntryArr2 = (DrawListObjectEntry[]) SpatialObjectIndex.this.objectsToUpdate.toArray(DrawListObjectEntry[SpatialObjectIndex.this.objectsToUpdate.size()])
                    drawListObjectEntryArr = (DrawListObjectEntry[]) SpatialObjectIndex.this.objectsToRemove.toArray(DrawListObjectEntry[SpatialObjectIndex.this.objectsToRemove.size()])
                    SpatialObjectIndex.this.objectsToUpdate.clear()
                    SpatialObjectIndex.this.objectsToRemove.clear()
                }
                Int i = 0
                for (DrawListObjectEntry drawListObjectEntry : drawListObjectEntryArr2) {
                    i |= !drawListObjectEntry.getObjectInfo().isDead ? SpatialObjectIndex.this.handleUpdateObject(drawListObjectEntry) : SpatialObjectIndex.this.handleRemoveObject(drawListObjectEntry)
                }
                for (DrawListObjectEntry -wrap0 : drawListObjectEntryArr) {
                    i |= SpatialObjectIndex.this.handleRemoveObject(-wrap0)
                }
                if (i != 0 || SpatialObjectIndex.this.spatialTree.isDrawListChanged() || SpatialObjectIndex.this.spatialTree.isTreeWalkNeeded()) {
                    SpatialObjectIndex.this.drawListUpdateRequested.set(true)
                }
            }
        }
    }

    constructor(drawableStore: DrawableStore, i: Int) {
        this.drawableStore = drawableStore
        this.avatarCountLimit = i
        this.spatialTree = SpatialTree(16, REGION_SIZE_XY, REGION_SIZE_XY, REGION_SIZE_Z, this)
        this.objectsInFrustrum = DrawList.create(drawableStore, null, i)
    }

    private fun getObjectsInCells(i: Int): DrawList {
        DrawList create = DrawList.create(this.drawableStore, this.objectsInFrustrum, i)
        this.spatialTree.addDrawables(create)
        create.initRenderPasses()
        return create
    }

    private fun handleRemoveObject(drawListObjectEntry: DrawListObjectEntry): Boolean {
        this.spatialTree.removeObject(drawListObjectEntry)
        drawListObjectEntry.getObjectInfo().clearDrawListEntry()
        return false
    }

    private fun handleUpdateObject(drawListObjectEntry: DrawListObjectEntry): Boolean {
        drawListObjectEntry.updateBoundingBox()
        this.spatialTree.updateObject(drawListObjectEntry)
        return false
    }

    private fun removeObject(drawListObjectEntry: DrawListObjectEntry): Unit {
        Int remove
        synchronized (this.objectUpdateRemoveLock) {
            remove = this.objectsToUpdate.remove(drawListObjectEntry) | this.objectsToRemove.add(drawListObjectEntry)
        }
        if (remove != 0 && this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask)
        }
    }

    fun completeInitialUpdate(): Unit {
        this.initialUpdateCompleted = true
        if (!this.indexDisabled) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask)
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate)
            this.drawListUpdateRequested.set(true)
        }
    }

    Unit disableIndex() {
        this.indexDisabled = true
    }

    DrawableAvatar getDrawableAvatar(SLObjectInfo sLObjectInfo) {
        return (DrawableAvatar) this.drawableStore.drawableAvatarCache.getIfPresent(sLObjectInfo)
    }

    fun getObjectsInFrustrum(): DrawList {
        return this.objectsInFrustrum
    }

    Unit requestEntryRemoval(DrawListEntry drawListEntry) {
        if (drawListEntry instanceof DrawListObjectEntry) {
            removeObject((DrawListObjectEntry) drawListEntry)
        }
    }

    fun setAvatarCountLimit(i: Int): Unit {
        this.avatarCountLimit = i
    }

    fun setViewport(frustrumInfo: FrustrumInfo, frustrumPlanes: FrustrumPlanes): Unit {
        Any obj = 1
        synchronized (this.lock) {
            if (this.frustrumInfo == null) {
                this.frustrumInfo = frustrumInfo
            } else if (this.frustrumInfo == frustrumInfo) {
                obj = null
            } else {
                this.frustrumInfo = frustrumInfo
            }
            if (obj != null) {
                this.frustrumPlanes = frustrumPlanes
                this.frustrumChanged.set(true)
                if (this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
                    this.drawListUpdateRequested.set(true)
                }
            }
        }
    }

    fun updateDrawListIfNeeded(): Boolean {
        if (!this.drawListUpdateRequested.getAndSet(false)) {
            return false
        }
        PrimComputeExecutor.getInstance().execute(this.drawListUpdateTask)
        return true
    }

    fun updateObject(drawListObjectEntry: DrawListObjectEntry): Unit {
        Boolean add
        synchronized (this.objectUpdateRemoveLock) {
            add = !drawListObjectEntry.getObjectInfo().isDead ? this.objectsToUpdate.add(drawListObjectEntry) : this.objectsToRemove.add(drawListObjectEntry)
        }
        if (add && this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask)
        }
    }

    Unit updateTerrainPatch(Int i, Int i2, TerrainData terrainData) {
        synchronized (this.terrainLock) {
            this.terrainDirty.put(Int.valueOf((i2 * 16) + i), terrainData)
        }
        if (this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate)
        }
    }
}
