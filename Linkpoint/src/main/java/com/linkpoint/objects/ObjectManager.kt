package com.linkpoint.objects

import android.os.Parcelable
import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.ObjectPropertyEntry
import com.linkpoint.protocol.messages.ObjectUpdateData
import com.linkpoint.protocol.messages.TerseUpdateData
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages scene objects and their properties
 * Handles object selection, editing, and interaction
 */
class ObjectManager(
    private val udpConnection: UDPConnectionFixed
) {
    companion object {
        private const val TAG = "ObjectManager"
        private val MESSAGE_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN
        private val ZERO_UUID = UUID(0L, 0L)
        
        // Diagnostic threshold for "recently updated" objects (5 seconds)
        private const val RECENT_UPDATE_THRESHOLD_MS = 5000L
        
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
     * Handle object properties update from ObjectProperties message.
     * Updates object name, description, owner, and permissions.
     */
    fun handleObjectProperties(props: ObjectPropertyEntry) {
        // Find object by UUID
        val obj = objectsByUUID[props.objectId]
        if (obj != null) {
            obj.apply {
                name = props.name
                description = props.description
                ownerId = props.ownerId
                lastUpdate = System.currentTimeMillis()
            }
            Log.d(TAG, "Updated properties for object '${props.name}' (${props.objectId})")
        } else {
            // Object not in scene yet - this can happen if properties arrive before ObjectUpdate
            Log.d(TAG, "Received properties for unknown object '${props.name}' (${props.objectId})")
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
        if (localIds.isEmpty()) return
        
        scope.launch {
            // Build ObjectSelect packet
            // Format: AgentData block + ObjectData blocks
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(16 + 4 * localIds.size).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder for agent ID (16 bytes)
            repeat(16) { payload.put(0) }
            
            // Object count
            payload.put(localIds.size.toByte())
            
            // ObjectData blocks - local IDs
            for (localId in localIds) {
                payload.putInt(localId)
            }
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_SELECT, payload.array(), reliable = true)
                Log.d(TAG, "Requested properties for ${localIds.size} objects")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request object properties", e)
            }
        }
    }
    
    /**
     * Send object update to server (position, rotation, scale)
     */
    private fun sendObjectUpdate(
        localId: Int,
        position: LLVector3? = null,
        rotation: LLQuaternion? = null,
        scale: LLVector3? = null
    ) {
        scope.launch {
            // MultipleObjectUpdate packet
            // Determine update type flags
            var updateType = 0
            if (position != null) updateType = updateType or 0x01  // Position
            if (rotation != null) updateType = updateType or 0x02  // Rotation
            if (scale != null) updateType = updateType or 0x04     // Scale
            
            if (updateType == 0) return@launch
            
            // Calculate payload size
            var dataSize = 0
            if (position != null) dataSize += 12  // 3 floats
            if (rotation != null) dataSize += 12  // 3 floats (quaternion compressed)
            if (scale != null) dataSize += 12     // 3 floats
            
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(16 + 1 + 4 + 1 + dataSize).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(16) { payload.put(0) }
            
            // Number of objects
            payload.put(1.toByte())
            
            // ObjectData block
            payload.putInt(localId)
            payload.put(updateType.toByte())
            
            // Write position if provided
            position?.let {
                payload.putFloat(it.x)
                payload.putFloat(it.y)
                payload.putFloat(it.z)
            }
            
            // Write rotation if provided
            rotation?.let {
                payload.putFloat(it.x)
                payload.putFloat(it.y)
                payload.putFloat(it.z)
            }
            
            // Write scale if provided
            scale?.let {
                payload.putFloat(it.x)
                payload.putFloat(it.y)
                payload.putFloat(it.z)
            }
            
            try {
                udpConnection.sendPacket(MessageIds.MULTIPLE_OBJECT_UPDATE, payload.array(), reliable = true)
                Log.d(TAG, "Sent object update for localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send object update", e)
            }
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
            // RezObject message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(100).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder (16 bytes agent, 16 bytes session, 16 bytes group)
            repeat(48) { payload.put(0) }
            
            // RezData
            payload.putUUID(itemId)
            
            // Position
            payload.putFloat(position.x)
            payload.putFloat(position.y)
            payload.putFloat(position.z)
            
            // Rotation
            payload.putFloat(rotation.x)
            payload.putFloat(rotation.y)
            payload.putFloat(rotation.z)
            
            // Flags
            payload.putInt(0)  // RezSelected = false
            
            try {
                udpConnection.sendPacket(MessageIds.REZ_OBJECT, payload.array(), reliable = true)
                Log.i(TAG, "Sent RezObject for item $itemId at $position")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to rez object", e)
            }
        }
    }
    
    /**
     * Take object to inventory
     */
    fun takeObject(localId: Int, folderId: UUID) {
        scope.launch {
            // DeRezObject message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(60).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(48) { payload.put(0) }
            
            // DeRezData
            payload.put(4)  // Destination = Take to inventory
            payload.putUUID(folderId)
            payload.putUUID(ZERO_UUID)
            
            // ObjectData
            payload.put(1)  // Number of objects
            payload.putInt(localId)
            
            try {
                udpConnection.sendPacket(MessageIds.DEREZ_OBJECT, payload.array(), reliable = true)
                Log.i(TAG, "Sent DeRezObject for localId=$localId to folder $folderId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to take object", e)
            }
        }
    }
    
    /**
     * Delete object
     */
    fun deleteObject(localId: Int) {
        scope.launch {
            // ObjectDelete message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(25).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(16) { payload.put(0) }
            
            // Force
            payload.put(0)
            
            // ObjectData
            payload.put(1)  // Number of objects
            payload.putInt(localId)
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_DELETE, payload.array(), reliable = true)
                
                // Remove from local cache
                objects.remove(localId)?.let { obj ->
                    objectsByUUID.remove(obj.fullId)
                }
                
                Log.i(TAG, "Deleted object localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete object", e)
            }
        }
    }
    
    /**
     * Link objects
     */
    fun linkObjects(localIds: List<Int>) {
        if (localIds.size < 2) return
        
        scope.launch {
            // ObjectLink message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(17 + 4 * localIds.size).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(16) { payload.put(0) }
            
            // ObjectData
            payload.put(localIds.size.toByte())
            for (localId in localIds) {
                payload.putInt(localId)
            }
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_LINK, payload.array(), reliable = true)
                Log.i(TAG, "Linked ${localIds.size} objects")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to link objects", e)
            }
        }
    }
    
    /**
     * Unlink objects
     */
    fun unlinkObjects(localIds: List<Int>) {
        if (localIds.isEmpty()) return
        
        scope.launch {
            // ObjectDelink message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(17 + 4 * localIds.size).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(16) { payload.put(0) }
            
            // ObjectData
            payload.put(localIds.size.toByte())
            for (localId in localIds) {
                payload.putInt(localId)
            }
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_DELINK, payload.array(), reliable = true)
                Log.i(TAG, "Unlinked ${localIds.size} objects")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unlink objects", e)
            }
        }
    }
    
    /**
     * Set object name
     */
    fun setObjectName(localId: Int, name: String) {
        objects[localId]?.name = name
        
        scope.launch {
            // ObjectName message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val nameBytes = name.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(17 + 4 + 1 + nameBytes.size).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(16) { payload.put(0) }
            
            // ObjectData
            payload.put(1)  // Number of objects
            payload.putInt(localId)
            payload.put(nameBytes.size.toByte())
            payload.put(nameBytes)
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_NAME, payload.array(), reliable = true)
                Log.d(TAG, "Set object name: localId=$localId, name=$name")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set object name", e)
            }
        }
    }
    
    /**
     * Set object description
     */
    fun setObjectDescription(localId: Int, description: String) {
        objects[localId]?.description = description
        
        scope.launch {
            // ObjectDescription message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val descBytes = description.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(17 + 4 + 1 + descBytes.size).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(16) { payload.put(0) }
            
            // ObjectData
            payload.put(1)  // Number of objects
            payload.putInt(localId)
            payload.put(descBytes.size.toByte())
            payload.put(descBytes)
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_DESCRIPTION, payload.array(), reliable = true)
                Log.d(TAG, "Set object description: localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set object description", e)
            }
        }
    }
    
    /**
     * Touch/click object
     */
    fun touchObject(localId: Int, position: LLVector3, normal: LLVector3, binormal: LLVector3) {
        scope.launch {
            // ObjectGrab message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val grabPayload = ByteBuffer.allocate(80).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(32) { grabPayload.put(0) }  // Agent + Session ID
            
            // ObjectData
            grabPayload.putInt(localId)
            
            // Touch vectors
            grabPayload.putFloat(position.x)
            grabPayload.putFloat(position.y)
            grabPayload.putFloat(position.z)
            
            grabPayload.putFloat(normal.x)
            grabPayload.putFloat(normal.y)
            grabPayload.putFloat(normal.z)
            
            grabPayload.putFloat(binormal.x)
            grabPayload.putFloat(binormal.y)
            grabPayload.putFloat(binormal.z)
            
            // Face index
            grabPayload.putInt(0)
            
            try {
                udpConnection.sendPacket(MessageIds.OBJECT_GRAB, grabPayload.array(), reliable = true)
                
                // Brief delay then release
                delay(100)
                
                // ObjectDeGrab message
                val degrabPayload = ByteBuffer.allocate(36).order(MESSAGE_BYTE_ORDER)
                repeat(32) { degrabPayload.put(0) }  // Agent + Session ID
                degrabPayload.putInt(localId)
                
                udpConnection.sendPacket(MessageIds.OBJECT_DEGRAB, degrabPayload.array(), reliable = true)
                Log.d(TAG, "Touched object localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to touch object", e)
            }
        }
    }
    
    /**
     * Sit on object
     */
    fun sitOnObject(localId: Int) {
        scope.launch {
            // AgentRequestSit message
            // NOTE: Second Life message blocks are little-endian; UUIDs remain raw big-endian bytes.
            val payload = ByteBuffer.allocate(44).order(MESSAGE_BYTE_ORDER)
            
            // AgentData - placeholder
            repeat(32) { payload.put(0) }  // Agent + Session ID
            
            // TargetObject
            val obj = objects[localId]
            if (obj != null) {
                payload.putUUID(obj.fullId)
            } else {
                payload.putUUID(ZERO_UUID)
            }
            
            // Offset
            payload.putFloat(0f)
            payload.putFloat(0f)
            payload.putFloat(0f)
            
            try {
                udpConnection.sendPacket(MessageIds.AGENT_REQUEST_SIT, payload.array(), reliable = true)
                Log.i(TAG, "Requested sit on localId=$localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sit on object", e)
            }
        }
    }
    
    /**
     * Get up from sitting.
     * Sends AgentSit message with SIT_FLAG_NO_FLAGS (0) to request standing.
     */
    fun standUp() {
        scope.launch {
            try {
                // AgentSit message to stand up (sending with no target ID indicates stand)
                val payload = ByteBuffer.allocate(33).order(MESSAGE_BYTE_ORDER)
                
                // AgentData block
                repeat(32) { payload.put(0) }  // Agent + Session ID placeholder
                
                // SitObject - ZERO_UUID indicates stand request
                payload.put(0)  // Flags = 0 (no sit flags, meaning stand)
                
                udpConnection.sendPacket(MessageIds.AGENT_SIT, payload.array(), reliable = true)
                Log.i(TAG, "Requested stand up")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stand up", e)
            }
        }
    }
    
    /**
     * Handle ObjectPropertiesFamily message from server.
     */
    fun handleObjectPropertiesFamily(data: com.linkpoint.protocol.messages.AdditionalMessageParsers.ObjectPropertiesFamilyData) {
        // Find object by UUID and update its properties
        val obj = objects.values.find { it.fullId == data.objectID }
        if (obj != null) {
            obj.name = data.name
            obj.description = data.description
            obj.ownerID = data.ownerID
            obj.groupID = data.groupID
            Log.d(TAG, "Updated object properties: ${data.name}")
        } else {
            Log.w(TAG, "ObjectPropertiesFamily for unknown object: ${data.objectID}")
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
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Get the total count of objects in the scene
     */
    fun getObjectCount(): Int = objects.size
    
    /**
     * Get the count of selected objects
     */
    fun getSelectedCount(): Int = _selectedObjects.value.size
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): ObjectManagerDiagnostics {
        val allObjects = objects.values.toList()
        val now = System.currentTimeMillis()
        
        val recentlyUpdated = allObjects.count { now - it.lastUpdate < RECENT_UPDATE_THRESHOLD_MS }
        val scriptedCount = allObjects.count { it.isScripted }
        val physicalCount = allObjects.count { it.isPhysical }
        
        return ObjectManagerDiagnostics(
            totalObjects = objects.size,
            objectsByUUID = objectsByUUID.size,
            selectedCount = _selectedObjects.value.size,
            isEditing = _isEditing.value,
            editMode = _editMode.value,
            recentlyUpdatedCount = recentlyUpdated,
            scriptedObjectCount = scriptedCount,
            physicalObjectCount = physicalCount
        )
    }
    
    /**
     * Diagnostic data class for object manager state
     */
    data class ObjectManagerDiagnostics(
        val totalObjects: Int,
        val objectsByUUID: Int,
        val selectedCount: Int,
        val isEditing: Boolean,
        val editMode: EditMode,
        val recentlyUpdatedCount: Int,
        val scriptedObjectCount: Int,
        val physicalObjectCount: Int
    )
}

@Parcelize
data class SceneObject(
    val localId: Int,
    val fullId: UUID,
    var ownerId: UUID? = null,
    var ownerID: UUID? = null, // Alias for compatibility
    var groupID: UUID? = null,
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
) : Parcelable {
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
