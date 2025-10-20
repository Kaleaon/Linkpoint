package com.linkpoint.objects

import com.linkpoint.Debug
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.LLQuaternion
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * PrimType - Primitive object types
 */
enum class PrimType(val typeId: Int) {
    BOX(0),
    CYLINDER(1),
    PRISM(2),
    SPHERE(3),
    TORUS(4),
    TUBE(5),
    RING(6),
    SCULPT(7),
    MESH(8);
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * SLObject - Represents an object in the world
 */
data class SLObject(
    val objectID: UUID,
    val localID: Int,
    val parentID: Int,
    var position: LLVector3,
    var rotation: LLQuaternion,
    var scale: LLVector3,
    var velocity: LLVector3,
    var angularVelocity: LLVector3,
    val ownerID: UUID,
    val creatorID: UUID,
    val groupID: UUID,
    var name: String,
    var description: String,
    val primType: PrimType,
    val material: Int,
    val flags: Int,
    val textureEntry: ByteArray?,
    val extraParams: ByteArray?,
    var lastUpdateTime: Long = System.currentTimeMillis()
) {
    // Object state
    var isPhantom = false
    var isPhysical = false
    var isTemporary = false
    var usePhysics = false
    var isAttachment = false
    
    // Visual state
    var isVisible = true
    var isSelected = false
    var isHighlighted = false
    
    // Children (for linksets)
    val children = mutableListOf<Int>()
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SLObject) return false
        return objectID == other.objectID
    }
    
    override fun hashCode(): Int {
        return objectID.hashCode()
    }
}

/**
 * ObjectUpdate - Object update message data
 */
data class ObjectUpdate(
    val localID: Int,
    val position: LLVector3?,
    val rotation: LLQuaternion?,
    val scale: LLVector3?,
    val velocity: LLVector3?,
    val angularVelocity: LLVector3?,
    val flags: Int?
)

/**
 * ObjectManager - Manages all objects in the world
 * 
 * Features:
 * - Object tracking
 * - Position/rotation updates
 * - Parent/child relationships
 * - Culling
 * - Selection
 * 
 * Based on Firestorm's LLViewerObjectList
 */
class ObjectManager {
    
    companion object {
        const val MAX_OBJECTS = 100000
        const val UPDATE_TIMEOUT_MS = 30000L
    }
    
    // Object storage
    private val objects = ConcurrentHashMap<UUID, SLObject>()
    private val objectsByLocalID = ConcurrentHashMap<Int, SLObject>()
    
    // Statistics
    private var totalObjectsCreated = 0
    private var totalObjectsDeleted = 0
    
    /**
     * Add or update object
     */
    fun addObject(obj: SLObject) {
        objects[obj.objectID] = obj
        objectsByLocalID[obj.localID] = obj
        totalObjectsCreated++
        
        // Add to parent's children if it has a parent
        if (obj.parentID != 0) {
            val parent = objectsByLocalID[obj.parentID]
            if (parent != null && !parent.children.contains(obj.localID)) {
                parent.children.add(obj.localID)
            }
        }
    }
    
    /**
     * Remove object
     */
    fun removeObject(objectID: UUID) {
        val obj = objects.remove(objectID)
        if (obj != null) {
            objectsByLocalID.remove(obj.localID)
            totalObjectsDeleted++
            
            // Remove from parent's children
            if (obj.parentID != 0) {
                val parent = objectsByLocalID[obj.parentID]
                parent?.children?.remove(obj.localID)
            }
            
            // Remove all children recursively
            for (childID in obj.children) {
                val child = objectsByLocalID[childID]
                if (child != null) {
                    removeObject(child.objectID)
                }
            }
        }
    }
    
    /**
     * Remove object by local ID
     */
    fun removeObjectByLocalID(localID: Int) {
        val obj = objectsByLocalID[localID]
        if (obj != null) {
            removeObject(obj.objectID)
        }
    }
    
    /**
     * Get object by UUID
     */
    fun getObject(objectID: UUID): SLObject? = objects[objectID]
    
    /**
     * Get object by local ID
     */
    fun getObjectByLocalID(localID: Int): SLObject? = objectsByLocalID[localID]
    
    /**
     * Update object
     */
    fun updateObject(update: ObjectUpdate) {
        val obj = objectsByLocalID[update.localID] ?: return
        
        update.position?.let { obj.position = it }
        update.rotation?.let { obj.rotation = it }
        update.scale?.let { obj.scale = it }
        update.velocity?.let { obj.velocity = it }
        update.angularVelocity?.let { obj.angularVelocity = it }
        update.flags?.let { 
            obj.flags or it
            updateFlags(obj, it)
        }
        
        obj.lastUpdateTime = System.currentTimeMillis()
    }
    
    /**
     * Update object flags
     */
    private fun updateFlags(obj: SLObject, flags: Int) {
        obj.isPhantom = (flags and 0x0001) != 0
        obj.isPhysical = (flags and 0x0002) != 0
        obj.isTemporary = (flags and 0x0004) != 0
        obj.usePhysics = (flags and 0x0010) != 0
        obj.isAttachment = (flags and 0x0020) != 0
    }
    
    /**
     * Get all objects
     */
    fun getAllObjects(): List<SLObject> = objects.values.toList()
    
    /**
     * Get objects in range
     */
    fun getObjectsInRange(center: LLVector3, radius: Float): List<SLObject> {
        return objects.values.filter {
            it.position.distance(center) <= radius
        }
    }
    
    /**
     * Get root objects (objects without parents)
     */
    fun getRootObjects(): List<SLObject> {
        return objects.values.filter { it.parentID == 0 }
    }
    
    /**
     * Get children of object
     */
    fun getChildren(obj: SLObject): List<SLObject> {
        return obj.children.mapNotNull { objectsByLocalID[it] }
    }
    
    /**
     * Get parent of object
     */
    fun getParent(obj: SLObject): SLObject? {
        return if (obj.parentID != 0) {
            objectsByLocalID[obj.parentID]
        } else null
    }
    
    /**
     * Select object
     */
    fun selectObject(objectID: UUID) {
        objects[objectID]?.isSelected = true
    }
    
    /**
     * Deselect object
     */
    fun deselectObject(objectID: UUID) {
        objects[objectID]?.isSelected = false
    }
    
    /**
     * Deselect all
     */
    fun deselectAll() {
        objects.values.forEach { it.isSelected = false }
    }
    
    /**
     * Get selected objects
     */
    fun getSelectedObjects(): List<SLObject> {
        return objects.values.filter { it.isSelected }
    }
    
    /**
     * Clear all objects
     */
    fun clear() {
        objects.clear()
        objectsByLocalID.clear()
    }
    
    /**
     * Cleanup old objects (remove objects not updated recently)
     */
    fun cleanupOldObjects(maxAgeMs: Long = UPDATE_TIMEOUT_MS) {
        val currentTime = System.currentTimeMillis()
        val toRemove = mutableListOf<UUID>()
        
        for (obj in objects.values) {
            if (currentTime - obj.lastUpdateTime > maxAgeMs) {
                toRemove.add(obj.objectID)
            }
        }
        
        for (objectID in toRemove) {
            removeObject(objectID)
        }
        
        if (toRemove.isNotEmpty()) {
            Debug.Log("Cleaned up ${toRemove.size} old objects")
        }
    }
    
    /**
     * Get statistics
     */
    fun getStats(): ObjectStats {
        return ObjectStats(
            totalObjects = objects.size,
            rootObjects = getRootObjects().size,
            selectedObjects = getSelectedObjects().size,
            totalCreated = totalObjectsCreated,
            totalDeleted = totalObjectsDeleted
        )
    }
    
    data class ObjectStats(
        val totalObjects: Int,
        val rootObjects: Int,
        val selectedObjects: Int,
        val totalCreated: Int,
        val totalDeleted: Int
    )
}
