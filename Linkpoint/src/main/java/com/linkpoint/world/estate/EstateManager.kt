package com.linkpoint.world.estate

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * EstateManager - Handles estate/region management functions.
 * 
 * Features:
 * - Estate info retrieval
 * - Estate banning/unbanning
 * - Estate access lists
 * - Region restart
 * - Covenant display
 * - Sun/time settings
 * - Terrain editing (for estate managers)
 * 
 * Based on reference viewer/Firestorm estate management.
 */
class EstateManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "EstateManager"
        
        // Estate flags
        const val FLAG_ALLOW_DIRECT_TELEPORT = 0x00000001L
        const val FLAG_DENY_ANONYMOUS = 0x00000002L
        const val FLAG_DENY_IDENTIFIED = 0x00000004L
        const val FLAG_DENY_TRANSACTED = 0x00000008L
        const val FLAG_ALLOW_PARCEL_CHANGES = 0x00000010L
        const val FLAG_ABUSE_EMAIL_TO_ESTATE_OWNER = 0x00000020L
        const val FLAG_ALLOW_VOICE = 0x00000040L
        const val FLAG_BLOCK_DWELL = 0x00000080L
        const val FLAG_RESET_HOME_ON_TELEPORT = 0x00000100L
        const val FLAG_ALLOW_LANDMARK = 0x00000200L
        const val FLAG_ALLOW_SET_HOME = 0x00000400L
        const val FLAG_SUN_FIXED = 0x00010000L
        const val FLAG_TAX_FREE = 0x00020000L
        const val FLAG_BLOCK_FLY = 0x00040000L
        const val FLAG_ALLOW_HIDE_PARCEL_OWNER = 0x00080000L
        const val FLAG_ALLOW_ENVIRONMENT_OVERRIDE = 0x00100000L
        const val FLAG_DENY_AGE_UNVERIFIED = 0x00400000L
        const val FLAG_SKIP_SCRIPTS = 0x10000000L
        const val FLAG_SKIP_COLLISIONS = 0x20000000L
        const val FLAG_SKIP_PHYSICS = 0x40000000L
        
        // Estate access flags
        const val ACCESS_ALLOWED = 1
        const val ACCESS_BANNED = 2
        const val ACCESS_MANAGER = 4
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Estate info
    private val _estateInfo = MutableStateFlow<EstateInfo?>(null)
    val estateInfo: StateFlow<EstateInfo?> = _estateInfo
    
    // Access lists
    private val _allowedAgents = MutableStateFlow<List<EstateAccessEntry>>(emptyList())
    val allowedAgents: StateFlow<List<EstateAccessEntry>> = _allowedAgents
    
    private val _bannedAgents = MutableStateFlow<List<EstateAccessEntry>>(emptyList())
    val bannedAgents: StateFlow<List<EstateAccessEntry>> = _bannedAgents
    
    private val _managers = MutableStateFlow<List<EstateAccessEntry>>(emptyList())
    val managers: StateFlow<List<EstateAccessEntry>> = _managers
    
    private val _allowedGroups = MutableStateFlow<List<UUID>>(emptyList())
    val allowedGroups: StateFlow<List<UUID>> = _allowedGroups
    
    // Covenant
    private val _covenantText = MutableStateFlow<String?>(null)
    val covenantText: StateFlow<String?> = _covenantText
    
    // Permission check
    private val _isEstateManager = MutableStateFlow(false)
    val isEstateManager: StateFlow<Boolean> = _isEstateManager
    
    private val _isEstateOwner = MutableStateFlow(false)
    val isEstateOwner: StateFlow<Boolean> = _isEstateOwner
    
    /**
     * Request estate info.
     */
    fun requestEstateInfo() {
        scope.launch {
            try {
                // 3 UUIDs (48 bytes) + Invoice int (4 bytes) = 52 bytes
                val payload = ByteBuffer.allocate(52).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // TransactionID
                payload.putUUID(UUID.randomUUID())
                
                // Invoice
                payload.putInt(0)
                
                udpConnection.sendPacket(MessageIdRegistry.ESTATE_OWNER_MESSAGE, payload.array(), reliable = true)
                Log.d(TAG, "Requested estate info")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request estate info", e)
            }
        }
    }
    
    /**
     * Handle estate info from simulator.
     */
    fun handleEstateInfo(
        estateId: Int,
        estateName: String,
        ownerId: UUID,
        flags: Long,
        sunHour: Float,
        parentEstateId: Int,
        covenantId: UUID?,
        covenantTimestamp: Long,
        abuseEmailAddress: String?
    ) {
        val info = EstateInfo(
            estateId = estateId,
            estateName = estateName,
            ownerId = ownerId,
            flags = flags,
            sunHour = sunHour,
            parentEstateId = parentEstateId,
            covenantId = covenantId,
            covenantTimestamp = covenantTimestamp,
            abuseEmailAddress = abuseEmailAddress
        )
        _estateInfo.value = info
        
        // Check if we're estate owner or manager
        _isEstateOwner.value = ownerId == agentId
        
        Log.d(TAG, "Estate info received: $estateName (ID: $estateId)")
    }
    
    /**
     * Update estate access lists.
     */
    fun handleEstateAccessDelta(accessType: Int, entries: List<EstateAccessEntry>) {
        when (accessType) {
            ACCESS_ALLOWED -> _allowedAgents.value = entries
            ACCESS_BANNED -> _bannedAgents.value = entries
            ACCESS_MANAGER -> {
                _managers.value = entries
                _isEstateManager.value = entries.any { it.agentId == agentId }
            }
        }
    }
    
    /**
     * Ban an agent from the estate.
     */
    fun banAgent(targetAgentId: UUID) {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                sendEstateAccessChange(targetAgentId, add = true, banned = true)
                Log.i(TAG, "Banned agent $targetAgentId from estate")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to ban agent", e)
            }
        }
    }
    
    /**
     * Unban an agent from the estate.
     */
    fun unbanAgent(targetAgentId: UUID) {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                sendEstateAccessChange(targetAgentId, add = false, banned = true)
                Log.i(TAG, "Unbanned agent $targetAgentId from estate")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unban agent", e)
            }
        }
    }
    
    /**
     * Add agent to estate access list.
     */
    fun addToAccessList(targetAgentId: UUID) {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                sendEstateAccessChange(targetAgentId, add = true, banned = false)
                Log.i(TAG, "Added agent $targetAgentId to estate access")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add agent to access list", e)
            }
        }
    }
    
    /**
     * Remove agent from estate access list.
     */
    fun removeFromAccessList(targetAgentId: UUID) {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                sendEstateAccessChange(targetAgentId, add = false, banned = false)
                Log.i(TAG, "Removed agent $targetAgentId from estate access")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove agent from access list", e)
            }
        }
    }
    
    /**
     * Initiate region restart (estate manager/owner only).
     */
    fun restartRegion(delaySeconds: Int = 120) {
        if (!canManageEstate()) {
            Log.w(TAG, "Cannot restart region - not estate manager")
            return
        }
        
        scope.launch {
            try {
                // Calculate buffer size: 3 UUIDs (48) + method (1+7) + param count (1) + delay string (1 + up to 10 chars) = ~70
                val payload = ByteBuffer.allocate(128).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // Invoice
                payload.putUUID(UUID.randomUUID())
                
                // Method
                val method = "restart".toByteArray()
                payload.put(method.size.toByte())
                payload.put(method)
                
                // Param: delay
                payload.put(1) // 1 param
                val delay = delaySeconds.toString().toByteArray()
                payload.put(delay.size.toByte())
                payload.put(delay)
                
                udpConnection.sendPacket(MessageIdRegistry.ESTATE_OWNER_MESSAGE, payload.array(), reliable = true)
                Log.i(TAG, "Initiated region restart with $delaySeconds second delay")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to restart region", e)
            }
        }
    }
    
    /**
     * Cancel pending region restart.
     */
    fun cancelRegionRestart() {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(60).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                payload.putUUID(UUID.randomUUID())
                
                // Method
                val method = "cancelrestart".toByteArray()
                payload.put(method.size.toByte())
                payload.put(method)
                
                payload.put(0) // No params
                
                udpConnection.sendPacket(MessageIdRegistry.ESTATE_OWNER_MESSAGE, payload.array(), reliable = true)
                Log.i(TAG, "Cancelled region restart")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel region restart", e)
            }
        }
    }
    
    /**
     * Kick user from region (teleport home).
     */
    fun kickUser(targetAgentId: UUID, reason: String = "") {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(100 + reason.length).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                payload.putUUID(UUID.randomUUID())
                
                // Method
                val method = "teleporthomeuser".toByteArray()
                payload.put(method.size.toByte())
                payload.put(method)
                
                // Params: preset (unused), target agent id
                payload.put(2)
                
                // Preset
                val preset = "0".toByteArray()
                payload.put(preset.size.toByte())
                payload.put(preset)
                
                // Target
                val target = targetAgentId.toString().toByteArray()
                payload.put(target.size.toByte())
                payload.put(target)
                
                udpConnection.sendPacket(MessageIdRegistry.ESTATE_OWNER_MESSAGE, payload.array(), reliable = true)
                Log.i(TAG, "Kicked user $targetAgentId from region")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to kick user", e)
            }
        }
    }
    
    /**
     * Freeze user (prevent movement/actions).
     */
    fun freezeUser(targetAgentId: UUID, freeze: Boolean) {
        if (!canManageEstate()) return
        
        scope.launch {
            try {
                val payload = ByteBuffer.allocate(80).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // TargetData
                payload.putUUID(targetAgentId)
                payload.putInt(if (freeze) 1 else 0) // Flags
                
                udpConnection.sendPacket(MessageIdRegistry.FREEZE_USER, payload.array(), reliable = true)
                Log.i(TAG, "${if (freeze) "Froze" else "Unfroze"} user $targetAgentId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to freeze/unfreeze user", e)
            }
        }
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private fun canManageEstate(): Boolean {
        return _isEstateOwner.value || _isEstateManager.value
    }
    
    private suspend fun sendEstateAccessChange(targetAgentId: UUID, add: Boolean, banned: Boolean) {
        // Wire format (LL message_template `EstateOwnerMessage`, low-freq 260;
        // Lumiya: slproto/messages/EstateOwnerMessage.java PackPayload):
        //   AgentData:  AgentID(LLUUID), SessionID(LLUUID), TransactionID(LLUUID)
        //   MethodData: Method(Variable 1 NUL-terminated), Invoice(LLUUID)
        //   ParamList (Variable, U8 count):
        //     Parameter(Variable 1 NUL-terminated)
        //
        // The previous encoder skipped the Invoice UUID and shipped the
        // Method/Parameter strings without trailing NULs, so the simulator
        // silently rejected the access-list update.

        val methodRaw = "estateaccessdelta".toByteArray(Charsets.UTF_8)
        val methodBytes = methodRaw + 0.toByte()

        // Operation flags: 1=add allowed, 2=remove allowed, 4=add banned, 8=remove banned
        val opFlag = when {
            add && !banned -> 1
            !add && !banned -> 2
            add && banned -> 4
            else -> 8
        }
        val opRaw = opFlag.toString().toByteArray(Charsets.UTF_8)
        val opBytes = opRaw + 0.toByte()
        val agentRaw = targetAgentId.toString().toByteArray(Charsets.UTF_8)
        val agentBytes = agentRaw + 0.toByte()

        val payload = ByteBuffer
            .allocate(48 /* AgentData */ + 1 + methodBytes.size + 16 /* Invoice */ +
                      1 /* param count */ + 1 + opBytes.size + 1 + agentBytes.size)
            .order(ByteOrder.LITTLE_ENDIAN)

        // AgentData
        payload.putUUID(agentId)
        payload.putUUID(udpConnection.getSessionId())
        payload.putUUID(UUID.randomUUID()) // TransactionID

        // MethodData
        payload.put(methodBytes.size.toByte())
        payload.put(methodBytes)
        payload.putUUID(UUID.randomUUID()) // Invoice (echoed in EstateOwnerMessageReply)

        // ParamList (Variable u8 count + entries)
        payload.put(2.toByte())
        payload.put(opBytes.size.toByte())
        payload.put(opBytes)
        payload.put(agentBytes.size.toByte())
        payload.put(agentBytes)

        udpConnection.sendPacket(MessageIdRegistry.ESTATE_OWNER_MESSAGE, payload.array(), reliable = true)
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

/**
 * Estate information.
 */
data class EstateInfo(
    val estateId: Int,
    val estateName: String,
    val ownerId: UUID,
    val flags: Long,
    val sunHour: Float,
    val parentEstateId: Int,
    val covenantId: UUID?,
    val covenantTimestamp: Long,
    val abuseEmailAddress: String?
) {
    val allowDirectTeleport: Boolean get() = (flags and EstateManager.FLAG_ALLOW_DIRECT_TELEPORT) != 0L
    val allowVoice: Boolean get() = (flags and EstateManager.FLAG_ALLOW_VOICE) != 0L
    val sunFixed: Boolean get() = (flags and EstateManager.FLAG_SUN_FIXED) != 0L
    val blockFly: Boolean get() = (flags and EstateManager.FLAG_BLOCK_FLY) != 0L
    val denyAgeUnverified: Boolean get() = (flags and EstateManager.FLAG_DENY_AGE_UNVERIFIED) != 0L
}

/**
 * Estate access list entry.
 */
data class EstateAccessEntry(
    val agentId: UUID,
    val name: String = "",
    val accessType: Int = 0,
    val addedTimestamp: Long = 0
)
