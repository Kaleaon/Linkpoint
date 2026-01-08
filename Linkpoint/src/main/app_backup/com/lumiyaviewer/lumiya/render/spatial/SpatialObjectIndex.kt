package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.res.collections.WeakQueue
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData
import java.util.Collections
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.concurrent.atomic.AtomicBoolean

class SpatialObjectIndex(
    val drawableStore: DrawableStore,
    limit: Int
) {
    private val NUM_DEPTH_BINS = 16
    private val REGION_SIZE_XY = 256.0f
    private val REGION_SIZE_Z = 4096.0f
    
    @Volatile
    var avatarCountLimit = limit
    
    private val drawListUpdateRequested = AtomicBoolean(false)
    private val drawListUpdateTask = DrawListUpdateTask()
    private val frustrumChanged = AtomicBoolean(false)
    
    @Volatile
    private var frustrumInfo: FrustrumInfo? = null
    @Volatile
    private var frustrumPlanes: FrustrumPlanes? = null
    
    @Volatile
    private var indexDisabled = false
    @Volatile
    private var initialUpdateCompleted = false
    
    private val lock = Any()
    private val objectUpdateRemoveLock = Any()
    
    @Volatile
    private var objectsInFrustrum: DrawList
    
    private val objectsToRemove: MutableSet<DrawListObjectEntry> = Collections.newSetFromMap(IdentityHashMap())
    private val objectsToUpdate: MutableSet<DrawListObjectEntry> = Collections.newSetFromMap(IdentityHashMap())
    private val objectsUpdateTask = ObjectsUpdateTask()
    
    lateinit var spatialTree: SpatialTree
    private val terrain = Array(16) { arrayOfNulls<DrawListTerrainEntry>(16) }
    private val terrainDirty = HashMap<Int, TerrainData>()
    private val terrainLock = Any()
    
    private val terrainUpdate = Runnable {
        var updateRequested = false
        while (initialUpdateCompleted && !indexDisabled) {
            var terrainData: TerrainData? = null
            var index = -1
            var hasData = false
            
            synchronized(terrainLock) {
                val it = terrainDirty.entries.iterator()
                if (it.hasNext()) {
                    val entry = it.next()
                    index = entry.key
                    terrainData = entry.value
                    it.remove()
                    hasData = true
                }
            }
            
            if (!hasData) break
            
            if (index >= 0 && terrainData != null) {
                val y = index / 16
                val x = index % 16
                val patchInfo = terrainData!!.getPatchInfo(x, y)
                
                if (patchInfo != null) {
                    var entry: DrawListTerrainEntry?
                    synchronized(terrainLock) {
                        entry = terrain[x][y]
                        if (entry == null) {
                            entry = DrawListTerrainEntry(patchInfo, x, y)
                            terrain[x][y] = entry
                        } else {
                            entry!!.updatePatchInfo(patchInfo)
                        }
                    }
                    spatialTree.updateObject(entry!!)
                } else {
                    var entry: DrawListTerrainEntry?
                    synchronized(terrainLock) {
                        entry = terrain[x][y]
                        terrain[x][y] = null
                    }
                    if (entry != null) {
                        spatialTree.removeObject(entry!!)
                    }
                }
                updateRequested = true
            }
        }
        if (updateRequested) {
            drawListUpdateRequested.set(true)
        }
    }

    init {
        this.spatialTree = SpatialTree(16, REGION_SIZE_XY, REGION_SIZE_XY, REGION_SIZE_Z, this)
        this.objectsInFrustrum = DrawList.create(drawableStore, null, limit)
    }

    private inner class DrawListUpdateTask : Runnable, WeakQueue.LowPriority {
        override fun run() {
            if (initialUpdateCompleted && !indexDisabled) {
                val needWalk = if (!frustrumChanged.getAndSet(false)) spatialTree.isTreeWalkNeeded() else true
                
                if (needWalk) {
                    val planes = frustrumPlanes
                    val info = frustrumInfo
                    if (planes != null && info != null) {
                        spatialTree.walkTree(planes, info.viewDistance)
                    }
                }
                
                if (spatialTree.isDrawListChanged()) {
                    objectsInFrustrum = getObjectsInCells(avatarCountLimit)
                }
            }
        }
    }

    private inner class ObjectsUpdateTask : Runnable {
        override fun run() {
            if (initialUpdateCompleted && !indexDisabled) {
                var toUpdate: Array<DrawListObjectEntry>
                var toRemove: Array<DrawListObjectEntry>
                
                synchronized(objectUpdateRemoveLock) {
                    toUpdate = objectsToUpdate.toTypedArray()
                    toRemove = objectsToRemove.toTypedArray()
                    objectsToUpdate.clear()
                    objectsToRemove.clear()
                }
                
                var changes = false
                for (entry in toUpdate) {
                    if (!entry.getObjectInfo().isDead) {
                        if (handleUpdateObject(entry)) changes = true
                    } else {
                        if (handleRemoveObject(entry)) changes = true
                    }
                }
                
                for (entry in toRemove) {
                    if (handleRemoveObject(entry)) changes = true
                }
                
                if (spatialTree.isDrawListChanged() || spatialTree.isTreeWalkNeeded()) {
                    drawListUpdateRequested.set(true)
                }
            }
        }
    }

    private fun getObjectsInCells(limit: Int): DrawList {
        val list = DrawList.create(drawableStore, objectsInFrustrum, limit)
        spatialTree.addDrawables(list)
        list.initRenderPasses()
        return list
    }

    private fun handleRemoveObject(entry: DrawListObjectEntry): Boolean {
        spatialTree.removeObject(entry)
        entry.getObjectInfo().clearDrawListEntry()
        return false
    }

    private fun handleUpdateObject(entry: DrawListObjectEntry): Boolean {
        entry.updateBoundingBox()
        spatialTree.updateObject(entry)
        return false
    }

    private fun removeObject(entry: DrawListObjectEntry) {
        var added: Boolean
        synchronized(objectUpdateRemoveLock) {
            objectsToUpdate.remove(entry)
            added = objectsToRemove.add(entry)
        }
        if (added && initialUpdateCompleted && !indexDisabled) {
            PrimComputeExecutor.getInstance().execute(objectsUpdateTask)
        }
    }

    fun completeInitialUpdate() {
        initialUpdateCompleted = true
        if (!indexDisabled) {
            PrimComputeExecutor.getInstance().execute(objectsUpdateTask)
            PrimComputeExecutor.getInstance().execute(terrainUpdate)
            drawListUpdateRequested.set(true)
        }
    }

    fun disableIndex() {
        indexDisabled = true
    }

    fun getDrawableAvatar(info: SLObjectInfo): DrawableAvatar? {
        return drawableStore.drawableAvatarCache.getIfPresent(info) as? DrawableAvatar
    }

    fun getObjectsInFrustrum(): DrawList {
        return objectsInFrustrum
    }

    fun requestEntryRemoval(entry: DrawListEntry) {
        if (entry is DrawListObjectEntry) {
            removeObject(entry)
        }
    }

    fun setViewport(info: FrustrumInfo, planes: FrustrumPlanes) {
        var changed = false
        synchronized(lock) {
            if (frustrumInfo == null || frustrumInfo != info) {
                frustrumInfo = info
                changed = true
            }
            
            if (changed) {
                frustrumPlanes = planes
                frustrumChanged.set(true)
                if (initialUpdateCompleted && !indexDisabled) {
                    drawListUpdateRequested.set(true)
                }
            }
        }
    }

    fun updateDrawListIfNeeded(): Boolean {
        if (!drawListUpdateRequested.getAndSet(false)) {
            return false
        }
        PrimComputeExecutor.getInstance().execute(drawListUpdateTask)
        return true
    }

    fun updateObject(entry: DrawListObjectEntry) {
        var added: Boolean
        synchronized(objectUpdateRemoveLock) {
            added = if (!entry.getObjectInfo().isDead) {
                objectsToUpdate.add(entry)
            } else {
                objectsToRemove.add(entry)
            }
        }
        if (added && initialUpdateCompleted && !indexDisabled) {
            PrimComputeExecutor.getInstance().execute(objectsUpdateTask)
        }
    }

    fun updateTerrainPatch(x: Int, y: Int, data: TerrainData?) {
        if (data == null) return
        synchronized(terrainLock) {
            terrainDirty[(y * 16) + x] = data
        }
        if (initialUpdateCompleted && !indexDisabled) {
            PrimComputeExecutor.getInstance().execute(terrainUpdate)
        }
    }
}
