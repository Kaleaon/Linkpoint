package com.linkpoint.render.spatial

import com.linkpoint.render.DrawableStore
import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.res.collections.WeakQueue.LowPriority
import com.linkpoint.res.executors.PrimComputeExecutor
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.terrain.TerrainData
import com.linkpoint.slproto.terrain.TerrainPatchInfo
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
    private const val NUM_DEPTH_BINS: Int = 16
    private const val REGION_SIZE_XY: Float = 256.0f
    private const val REGION_SIZE_Z: Float = 4096.0f
    private volatile Int avatarCountLimit = 5
    private val AtomicBoolean drawListUpdateRequested = AtomicBoolean(false)
    private val DrawListUpdateTask drawListUpdateTask = DrawListUpdateTask(this, null)
    private val DrawableStore drawableStore
    private val AtomicBoolean frustrumChanged = AtomicBoolean(false)
    private volatile FrustrumInfo frustrumInfo = null
    private volatile FrustrumPlanes frustrumPlanes = null
    private volatile Boolean indexDisabled = false
    private volatile Boolean initialUpdateCompleted = false
    private val Object lock = Object()
    private val Object objectUpdateRemoveLock = Object()
    private volatile DrawList objectsInFrustrum
    private val Set<DrawListObjectEntry> objectsToRemove = Collections.newSetFromMap(IdentityHashMap())
    private val Set<DrawListObjectEntry> objectsToUpdate = Collections.newSetFromMap(IdentityHashMap())
    private val ObjectsUpdateTask objectsUpdateTask = ObjectsUpdateTask(this, null)
    private val SpatialTree spatialTree
    private val Array<DrawListTerrainEntry>[] terrain = ((Array<DrawListTerrainEntry>[]) Array.newInstance(DrawListTerrainEntry.class, IntArray{16, 16}))
    private val Map<Integer, TerrainData> terrainDirty = HashMap()
    private val Object terrainLock = Object()
    private val Runnable terrainUpdate = Runnable() {
        fun run() {
            val z: Boolean = false
            while (SpatialObjectIndex.this.initialUpdateCompleted && (SpatialObjectIndex.this.indexDisabled ^ 1) != 0) {
                TerrainData terrainData
                synchronized (SpatialObjectIndex.this.terrainLock) {
                    val it: Iterator = SpatialObjectIndex.this.terrainDirty.entrySet().iterator()
                    if (it.hasNext()) {
                        val entry: Entry = (Entry) it.next()
                        val intValue: Int = ((Integer) entry.getKey()).intValue()
                        val terrainData2: TerrainData = (TerrainData) entry.getValue()
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
                    val i2: Int = i % 16
                    i /= 16
                    val patchInfo: TerrainPatchInfo = terrainData.getPatchInfo(i2, i)
                    DrawListEntry drawListEntry
                    if (patchInfo != null) {
                        synchronized (SpatialObjectIndex.this.terrainLock) {
                            drawListEntry = SpatialObjectIndex.this.terrain[i2][i]
                            if (drawListEntry == null) {
                                val drawListTerrainEntryArr: Array<DrawListTerrainEntry> = SpatialObjectIndex.this.terrain[i2]
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

        fun run() {
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

        fun run() {
            if (SpatialObjectIndex.this.initialUpdateCompleted && (SpatialObjectIndex.this.indexDisabled ^ 1) != 0) {
                Array<DrawListObjectEntry> drawListObjectEntryArr
                synchronized (SpatialObjectIndex.this.objectUpdateRemoveLock) {
                    val drawListObjectEntryArr2: Array<DrawListObjectEntry> = (Array<DrawListObjectEntry>) SpatialObjectIndex.this.objectsToUpdate.toArray(DrawListObjectEntry[SpatialObjectIndex.this.objectsToUpdate.size()])
                    drawListObjectEntryArr = (Array<DrawListObjectEntry>) SpatialObjectIndex.this.objectsToRemove.toArray(DrawListObjectEntry[SpatialObjectIndex.this.objectsToRemove.size()])
                    SpatialObjectIndex.this.objectsToUpdate.clear()
                    SpatialObjectIndex.this.objectsToRemove.clear()
                }
                val i: Int = 0
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

    public SpatialObjectIndex(DrawableStore drawableStore, Int i) {
        this.drawableStore = drawableStore
        this.avatarCountLimit = i
        this.spatialTree = SpatialTree(16, REGION_SIZE_XY, REGION_SIZE_XY, REGION_SIZE_Z, this)
        this.objectsInFrustrum = DrawList.create(drawableStore, null, i)
    }

     private fun getObjectsInCells(i: Int): DrawList {
        val create: DrawList = DrawList.create(this.drawableStore, this.objectsInFrustrum, i)
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

     private fun removeObject(drawListObjectEntry: DrawListObjectEntry) {
        Int remove
        synchronized (this.objectUpdateRemoveLock) {
            remove = this.objectsToUpdate.remove(drawListObjectEntry) | this.objectsToRemove.add(drawListObjectEntry)
        }
        if (remove != 0 && this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask)
        }
    }

    fun completeInitialUpdate() {
        this.initialUpdateCompleted = true
        if (!this.indexDisabled) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask)
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate)
            this.drawListUpdateRequested.set(true)
        }
    }

     fun disableIndex() {
        this.indexDisabled = true
    }

     fun getDrawableAvatar(sLObjectInfo: SLObjectInfo): DrawableAvatar {
        return (DrawableAvatar) this.drawableStore.drawableAvatarCache.getIfPresent(sLObjectInfo)
    }

     public fun getObjectsInFrustrum(): DrawList {
        return this.objectsInFrustrum
    }

     fun requestEntryRemoval(drawListEntry: DrawListEntry) {
        if (drawListEntry instanceof DrawListObjectEntry) {
            removeObject((DrawListObjectEntry) drawListEntry)
        }
    }

    fun setAvatarCountLimit(i: Int) {
        this.avatarCountLimit = i
    }

    fun setViewport(frustrumInfo: FrustrumInfo, frustrumPlanes: FrustrumPlanes) {
        val obj: Object = 1
        synchronized (this.lock) {
            if (this.frustrumInfo == null) {
                this.frustrumInfo = frustrumInfo
            } else if (this.frustrumInfo.equals(frustrumInfo)) {
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

     public fun updateDrawListIfNeeded(): Boolean {
        if (!this.drawListUpdateRequested.getAndSet(false)) {
            return false
        }
        PrimComputeExecutor.getInstance().execute(this.drawListUpdateTask)
        return true
    }

    fun updateObject(drawListObjectEntry: DrawListObjectEntry) {
        Boolean add
        synchronized (this.objectUpdateRemoveLock) {
            add = !drawListObjectEntry.getObjectInfo().isDead ? this.objectsToUpdate.add(drawListObjectEntry) : this.objectsToRemove.add(drawListObjectEntry)
        }
        if (add && this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
            PrimComputeExecutor.getInstance().execute(this.objectsUpdateTask)
        }
    }

     fun updateTerrainPatch(i: Int, i2: Int, terrainData: TerrainData) {
        synchronized (this.terrainLock) {
            this.terrainDirty.put(Integer.valueOf((i2 * 16) + i), terrainData)
        }
        if (this.initialUpdateCompleted && (this.indexDisabled ^ 1) != 0) {
            PrimComputeExecutor.getInstance().execute(this.terrainUpdate)
        }
    }
}
