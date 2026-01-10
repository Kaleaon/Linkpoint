package com.linkpoint.objects

import android.util.Log
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.TerseUpdateData
import com.linkpoint.protocol.messages.UDPConnection
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages scene objects and their properties
 * Handles object selection, editing, and interaction
 */
class ObjectManager(
    private val udpConnection: UDPConnection
) {
    companion object {
        private const val TAG = "ObjectManager"
        
        // Object update flags
        const val FLAG_USE_PHYSICS = 0x00000001
        const val FLAG_CREATE_SELECTED = 0x00000002
        const val FLAG_OBJECT_MODIFY = 0x00000004
        const val FLAG_OBJECT_COPY = 0x00000008
        const val FLAG_OBJECT_ANY_OWNER = 0x00000010
        const val FLAG_OBJECT_YOU_OWNER = 0x00000020
        const val FLAG_SCRIPTED = 0x00000040
        const val FLAG_HANDLE_TOUCH = 0x00000080
        const val FLAG_OBJECT_MOVE = 0x00000100
        const val FLAG_TAKES_MONEY = 0x00000200
        const val FLAG_PHANTOM = 0x00000400
        const val FLAG_INVENTORY_EMPTY = 0x00000800
        const val FLAG_JOINT_HINGE = 0x00001000
        const val FLAG_JOINT_P2P = 0x00002000
        const val FLAG_JOINT_LP2P = 0x00004000
        const val FLAG_JOINT_WHEEL = 0x00008000
        const val FLAG_ALLOW_INVENTORY_DROP = 0x00010000
        const val FLAG_OBJECT_TRANSFER = 0x00020000
        const val FLAG_OBJECT_GROUP_OWNED = 0x00040000
        const val FLAG_OBJECT_YOU_OFFICER = 0x00080000
        const val FLAG_CAMERA_DECOUPLED = 0x00100000
        const val FLAG_ANIM_SOURCE = 0x00200000
        const val FLAG_CAMERA_SOURCE = 0x00400000
        const val FLAG_TEMPORARY = 0x01000000
        const val FLAG_TEMPORARY_ON_REZ = 0x02000000
        const val FLAG_ZLIB_COMPRESSED = 0x04000000
        const val FLAG_LOCAL = 0x08000000
        const val FLAG_MEDIA_URL = 0x10000000
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // All objects in scene
    private val objects = ConcurrentHashMap<Int, SceneObject>()
    private val objectsByUUID = ConcurrentHashMap<UUID, SceneObject>()
    
    // Selection state
    private val _selectedObjects = MutableStateFlow<List<Int>>(emptyList())
    val selectedObjects: StateFlow<List<Int>> = _selectedObjects
    
    // Edit mode
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing
    
    private val _editMode = MutableStateFlow(EditMode.POSITION)
    val editMode: StateFlow<EditMode> = _editMode
    
    /**
     * Handle object update from simulator
     */
    fun handleObjectUpdate(data: ObjectUpdateData) {
        val obj = objects.getOrPut(data.localId) {
            SceneObject(
                localId = data.localId,
                fullId = data.fullId,
                ownerId = data.ownerId
            )
        }
        
        obj.apply {
            parentId = data.parentId
            position = data.position
            rotation = data.rotation
            velocity = data.velocity
            scale = data.scale
            pcode = data.pcode
            material = data.material
            clickAction = data.clickAction
            updateFlags = data.updateFlags
            textureEntry = data.textureEntry
            hoverText = data.hoverText
            hoverTextColor = data.hoverTextColor
            mediaUrl = data.mediaUrl
            nameValue = data.nameValue
            lastUpdate = System.currentTimeMillis()
        }
        
        objectsByUUID[data.fullId] = obj
    }
    
    /**
     * Handle terse position update
     */
    fun handleTerseUpdate(data: TerseUpdateData) {
        objects[data.localId]?.apply {
            position = data.position
            rotation = data.rotation
            velocity = data.velocity
            angularVelocity = data.angularVelocity
            lastUpdate = System.currentTimeMillis()
        }
    }
    
    /**
     * Remove object
     */
    fun removeObject(localId: Int) {
        objects.remove(localId)?.let { obj ->
            objectsByUUID.remove(obj.fullId)
        }
    }
    
    /**
     * Get object by local ID
     */
    fun getObject(localId: Int): SceneObject? = objects[localId]
    
    /**
     * Get object by UUID
     */
    fun getObjectByUUID(fullId: UUID): SceneObject? = objectsByUUID[fullId]
    
    /**
     * Get all objects
     */
    fun getAllObjects(): Collection<SceneObject> = objects.values
    
    /**
     * Select objects
     */
    fun selectObjects(localIds: List<Int>) {
        _selectedObjects.value = localIds
        
        if (localIds.isNotEmpty()) {
            // Request full object properties from server
            requestObjectProperties(localIds)
        }
    }
    
    /**
     * Add to selection
     */
    fun addToSelection(localId: Int) {
        _selectedObjects.value = _selectedObjects.value + localId
    }
    
    /**
     * Remove from selection
     */
    fun removeFromSelection(localId: Int) {
        _selectedObjects.value = _selectedObjects.value - localId
    }
    
    /**
     * Clear selection
     */
    fun clearSelection() {
        _selectedObjects.value = emptyList()
    }
    
    /**
     * Start editing
     */
    fun startEditing() {
        if (_selectedObjects.value.isNotEmpty()) {
            _isEditing.value = true
        }
    }
    
    /**
     * Stop editing
     */
    fun stopEditing() {
        _isEditing.value = false
    }
    
    /**
     * Set edit mode
     */
    fun setEditMode(mode: EditMode) {
        _editMode.value = mode
    }
    
    /**
     * Move selected objects
     */
    fun moveSelectedObjects(delta: LLVector3) {
        for (localId in _selectedObjects.value) {
            val obj = objects[localId] ?: continue
            obj.position = obj.position + delta
            
            // Send update to server
            sendObjectUpdate(localId, position = obj.position)
        }
    }
    
    /**
     * Rotate selected objects
     */
    fun rotateSelectedObjects(delta: LLQuaternion) {
        for (localId in _selectedObjects.value) {
            val obj = objects[localId] ?: continue
            obj.rotation = delta * obj.rotation
            
            sendObjectUpdate(localId, rotation = obj.rotation)
        }
    }
    
    /**
     * Scale selected objects
     */
    fun scaleSelectedObjects(factor: LLVector3) {
        for (localId in _selectedObjects.value) {
            val obj = objects[localId] ?: continue
            obj.scale = LLVector3(
                obj.scale.x * factor.x,
                obj.scale.y * factor.y,
                obj.scale.z * factor.z
            )
            
            sendObjectUpdate(localId, scale = obj.scale)
        }
    }
    
    /**
     * Request object properties
     */
    private fun requestObjectProperties(localIds: List<Int>) {
        // Build ObjectSelect packet
        scope.launch {
            // Would send ObjectSelect message
        }
    }
    
    /**
     * Send object update to server
     */
    private fun sendObjectUpdate(
        localId: Int,
        position: LLVector3? = null,
        rotation: LLQuaternion? = null,
        scale: LLVector3? = null
    ) {
        scope.launch {
            // Build MultipleObjectUpdate packet
            // Would send via UDP
        }
    }
    
    /**
     * Rez object from inventory
     */
    fun rezObject(
        itemId: UUID,
        position: LLVector3,
        rotation: LLQuaternion = LLQuaternion.identity()
    ) {
        scope.launch {
            // Would send RezObject message
        }
    }
    
    /**
     * Take object to inventory
     */
    fun takeObject(localId: Int, folderId: UUID) {
        scope.launch {
            // Would send DeRezObject message
        }
    }
    
    /**
     * Delete object
     */
    fun deleteObject(localId: Int) {
        scope.launch {
            // Would send ObjectDelete message
        }
    }
    
    /**
     * Link objects
     */
    fun linkObjects(localIds: List<Int>) {
        if (localIds.size < 2) return
        
        scope.launch {
            // Would send ObjectLink message
        }
    }
    
    /**
     * Unlink objects
     */
    fun unlinkObjects(localIds: List<Int>) {
        scope.launch {
            // Would send ObjectUnlink message
        }
    }
    
    /**
     * Set object name
     */
    fun setObjectName(localId: Int, name: String) {
        objects[localId]?.name = name
        
        scope.launch {
            // Would send ObjectName message
        }
    }
    
    /**
     * Set object description
     */
    fun setObjectDescription(localId: Int, description: String) {
        objects[localId]?.description = description
        
        scope.launch {
            // Would send ObjectDescription message
        }
    }
    
    /**
     * Touch/click object
     */
    fun touchObject(localId: Int, position: LLVector3, normal: LLVector3, binormal: LLVector3) {
        scope.launch {
            // Would send ObjectGrab/ObjectDeGrab messages
        }
    }
    
    /**
     * Sit on object
     */
    fun sitOnObject(localId: Int) {
        scope.launch {
            // Would send AgentRequestSit message
        }
    }
    
    /**
     * Get up from sitting
     */
    fun standUp() {
        scope.launch {
            // Would send AgentRequestStand message
        }
    }
    
    /**
     * Ray cast to find object at screen position
     */
    fun raycast(
        origin: LLVector3,
        direction: LLVector3,
        maxDistance: Float = 100f
    ): RaycastResult? {
        var closestHit: RaycastResult? = null
        var closestDistance = maxDistance
        
        for (obj in objects.values) {
            val distance = rayBoxIntersect(
                origin, direction,
                obj.position - obj.scale * 0.5f,
                obj.position + obj.scale * 0.5f
            )
            
            if (distance != null && distance < closestDistance) {
                closestDistance = distance
                closestHit = RaycastResult(
                    localId = obj.localId,
                    fullId = obj.fullId,
                    hitPosition = origin + direction * distance,
                    distance = distance
                )
            }
        }
        
        return closestHit
    }
    
    private fun rayBoxIntersect(
        origin: LLVector3,
        direction: LLVector3,
        boxMin: LLVector3,
        boxMax: LLVector3
    ): Float? {
        // Simple AABB ray intersection
        val t1 = (boxMin.x - origin.x) / direction.x
        val t2 = (boxMax.x - origin.x) / direction.x
        val t3 = (boxMin.y - origin.y) / direction.y
        val t4 = (boxMax.y - origin.y) / direction.y
        val t5 = (boxMin.z - origin.z) / direction.z
        val t6 = (boxMax.z - origin.z) / direction.z
        
        val tmin = maxOf(minOf(t1, t2), minOf(t3, t4), minOf(t5, t6))
        val tmax = minOf(maxOf(t1, t2), maxOf(t3, t4), maxOf(t5, t6))
        
        return if (tmax < 0 || tmin > tmax) null else tmin
    }
    
    fun shutdown() {
        scope.cancel()
        objects.clear()
        objectsByUUID.clear()
    }
}

data class SceneObject(
    val localId: Int,
    val fullId: UUID,
    var ownerId: UUID? = null,
    var parentId: Int = 0,
    var position: LLVector3 = LLVector3.zero(),
    var rotation: LLQuaternion = LLQuaternion.identity(),
    var velocity: LLVector3 = LLVector3.zero(),
    var angularVelocity: LLVector3 = LLVector3.zero(),
    var scale: LLVector3 = LLVector3(1f, 1f, 1f),
    var pcode: Int = 9,
    var material: Int = 0,
    var clickAction: Int = 0,
    var updateFlags: Int = 0,
    var textureEntry: ByteArray = ByteArray(0),
    var hoverText: String = "",
    var hoverTextColor: com.linkpoint.protocol.types.LLColor4 = com.linkpoint.protocol.types.LLColor4.white(),
    var mediaUrl: String = "",
    var nameValue: String = "",
    var name: String = "",
    var description: String = "",
    var lastUpdate: Long = 0
) {
    val isPhysical: Boolean get() = (updateFlags and ObjectManager.FLAG_USE_PHYSICS) != 0
    val isPhantom: Boolean get() = (updateFlags and ObjectManager.FLAG_PHANTOM) != 0
    val isTemporary: Boolean get() = (updateFlags and ObjectManager.FLAG_TEMPORARY) != 0
    val isScripted: Boolean get() = (updateFlags and ObjectManager.FLAG_SCRIPTED) != 0
    val isModify: Boolean get() = (updateFlags and ObjectManager.FLAG_OBJECT_MODIFY) != 0
    val isCopy: Boolean get() = (updateFlags and ObjectManager.FLAG_OBJECT_COPY) != 0
    val isTransfer: Boolean get() = (updateFlags and ObjectManager.FLAG_OBJECT_TRANSFER) != 0
}

data class RaycastResult(
    val localId: Int,
    val fullId: UUID,
    val hitPosition: LLVector3,
    val distance: Float
)

enum class EditMode {
    POSITION, ROTATION, SCALE, FOCUS, ALIGN
}
