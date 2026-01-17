package com.linkpoint.objects

import android.util.Log
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnection
import com.linkpoint.protocol.types.LLQuaternion
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * SitManager - Handles avatar sitting on objects.
 * 
 * Features:
 * - Sit on object
 * - Stand up
 * - Sit target handling
 * - Ground sit
 * - Sit position/rotation
 * 
 * Based on Lumiya sit implementation.
 */
class SitManager(
    private val udpConnection: UDPConnection,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "SitManager"
        
        // Sit flags
        const val AGENT_CONTROL_SIT_ON_GROUND = 0x10000000
        const val AGENT_CONTROL_STAND_UP = 0x20000000
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Sitting state
    private val _isSitting = MutableStateFlow(false)
    val isSitting: StateFlow<Boolean> = _isSitting
    
    private val _sitTargetId = MutableStateFlow<UUID?>(null)
    val sitTargetId: StateFlow<UUID?> = _sitTargetId
    
    private val _sitTargetLocalId = MutableStateFlow<Int?>(null)
    val sitTargetLocalId: StateFlow<Int?> = _sitTargetLocalId
    
    /**
     * Sit on an object by UUID.
     */
    fun sitOnObject(targetId: UUID) {
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // TargetObject
                payload.putUUID(targetId)
                
                // Offset (optional, use default)
                payload.putFloat(0f)
                payload.putFloat(0f)
                payload.putFloat(0f)
                
                udpConnection.sendPacket(MessageIds.AGENT_REQUEST_SIT, payload.array(), reliable = true)
                Log.i(TAG, "Requested sit on object $targetId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request sit", e)
            }
        }
    }
    
    /**
     * Sit on an object by local ID.
     */
    fun sitOnObjectByLocalId(localId: Int) {
        scope.launch {
            try {
                // Need to find UUID from local ID first
                // For now, store local ID
                _sitTargetLocalId.value = localId
                
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // Use AgentRequestSit with object local ID
                // This requires resolving the UUID
                
                udpConnection.sendPacket(MessageIds.AGENT_REQUEST_SIT, payload.array(), reliable = true)
                Log.d(TAG, "Requested sit on local ID $localId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sit on local ID", e)
            }
        }
    }
    
    /**
     * Accept sit request (sent after AgentRequestSit).
     */
    fun acceptSit() {
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                udpConnection.sendPacket(MessageIds.AGENT_SIT, payload.array(), reliable = true)
                Log.d(TAG, "Accepted sit")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to accept sit", e)
            }
        }
    }
    
    /**
     * Stand up from current sit target.
     */
    fun standUp() {
        scope.launch {
            try {
                // Send stand up control flag via AgentUpdate
                _isSitting.value = false
                _sitTargetId.value = null
                _sitTargetLocalId.value = null
                
                // Send AgentSit with stand flag - handled via control flags in AgentUpdate
                val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                payload.putInt(AGENT_CONTROL_STAND_UP)
                
                udpConnection.sendPacket(MessageIds.AGENT_SIT, payload.array(), reliable = true)
                
                Log.i(TAG, "Requested stand up")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stand up", e)
            }
        }
    }
    
    /**
     * Sit on ground at current position.
     */
    fun sitOnGround() {
        scope.launch {
            try {
                // Send ground sit control flag via AgentUpdate
                val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                payload.putInt(AGENT_CONTROL_SIT_ON_GROUND)
                
                udpConnection.sendPacket(MessageIds.AGENT_SIT, payload.array(), reliable = true)
                
                _isSitting.value = true
                _sitTargetId.value = null
                Log.d(TAG, "Requested ground sit")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sit on ground", e)
            }
        }
    }
    
    /**
     * Handle sit response from server.
     */
    fun handleAvatarSitResponse(
        sitObjectId: UUID,
        autopilot: Boolean,
        sitPosition: LLVector3,
        sitRotation: LLQuaternion,
        cameraEyeOffset: LLVector3,
        cameraAtOffset: LLVector3,
        forceMouselook: Boolean
    ) {
        _isSitting.value = true
        _sitTargetId.value = sitObjectId
        
        Log.d(TAG, "Avatar sit response: target=$sitObjectId, pos=$sitPosition")
    }
    
    /**
     * Update sitting state from avatar update.
     */
    fun updateSittingState(parentId: Int) {
        val wasSitting = _isSitting.value
        val nowSitting = parentId != 0
        
        if (wasSitting != nowSitting) {
            _isSitting.value = nowSitting
            if (!nowSitting) {
                _sitTargetId.value = null
                _sitTargetLocalId.value = null
            }
            Log.d(TAG, "Sitting state changed: $nowSitting")
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}
