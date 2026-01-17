package com.linkpoint.teleport

import android.util.Log
import com.linkpoint.core.RegionInfo
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * TeleportManager - Handles all teleportation functionality for Second Life.
 * 
 * Supports:
 * - Teleporting by region name and coordinates
 * - Teleporting by SLURL
 * - Teleporting by landmark
 * - Teleport home
 * - Teleport lure (offer to teleport another user)
 * - Accepting/declining teleport offers
 * - Teleport progress tracking
 * 
 * Based on Lumiya's teleport implementation.
 */
class TeleportManager(
    private val udpConnection: UDPConnection,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    // Get session ID from UDP connection
    private val sessionId: UUID
        get() = udpConnection.getSessionId()
    
    companion object {
        private const val TAG = "TeleportManager"
        
        // Teleport flags
        const val TELEPORT_FLAGS_SET_HOME = 0x00000001
        const val TELEPORT_FLAGS_SET_LAST_LOCATION = 0x00000002
        const val TELEPORT_FLAGS_VIA_LURE = 0x00000004
        const val TELEPORT_FLAGS_VIA_LANDMARK = 0x00000008
        const val TELEPORT_FLAGS_VIA_LOCATION = 0x00000010
        const val TELEPORT_FLAGS_VIA_HOME = 0x00000020
        const val TELEPORT_FLAGS_VIA_TELEHUB = 0x00000040
        const val TELEPORT_FLAGS_VIA_LOGIN = 0x00000080
        const val TELEPORT_FLAGS_VIA_GODLIKE_LURE = 0x00000100
        const val TELEPORT_FLAGS_GODLIKE = 0x00000200
        const val TELEPORT_FLAGS_DIRECT_LURE = 0x00000400
        const val TELEPORT_FLAGS_VIA_REGION_ID = 0x00000800
        const val TELEPORT_FLAGS_DISABLE_CANCEL = 0x00001000
        
        // Teleport states (from viewer code)
        const val TELEPORT_NONE = 0
        const val TELEPORT_START = 1
        const val TELEPORT_REQUESTED = 2
        const val TELEPORT_MOVING = 3
        const val TELEPORT_START_ARRIVAL = 4
        const val TELEPORT_ARRIVING = 5
        const val TELEPORT_LOCAL = 6
        const val TELEPORT_PENDING = 7
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Teleport state
    private val _teleportState = MutableStateFlow(TeleportState.IDLE)
    val teleportState: StateFlow<TeleportState> = _teleportState
    
    // Teleport events
    private val _teleportEvents = MutableSharedFlow<TeleportEvent>(replay = 0, extraBufferCapacity = 16)
    val teleportEvents: SharedFlow<TeleportEvent> = _teleportEvents
    
    // Current teleport progress message
    private val _progressMessage = MutableStateFlow("")
    val progressMessage: StateFlow<String> = _progressMessage
    
    // Pending teleport offer
    private var pendingLure: TeleportLure? = null
    
    init {
        // Register for teleport-related events from the event queue
        capabilityManager.registerEventHandler("TeleportProgress", this)
        capabilityManager.registerEventHandler("TeleportLocal", this)
        capabilityManager.registerEventHandler("TeleportFailed", this)
        capabilityManager.registerEventHandler("TeleportFinish", this)
        capabilityManager.registerEventHandler("TeleportStart", this)
        capabilityManager.registerEventHandler("EstablishAgentCommunication", this)
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        when (message) {
            "TeleportProgress" -> handleTeleportProgress(body)
            "TeleportLocal" -> handleTeleportLocal(body)
            "TeleportFailed" -> handleTeleportFailed(body)
            "TeleportFinish" -> handleTeleportFinish(body)
            "TeleportStart" -> handleTeleportStart(body)
            "EstablishAgentCommunication" -> handleEstablishAgentCommunication(body)
        }
    }
    
    // ==================== TELEPORT METHODS ====================
    
    /**
     * Teleport to a location by region name and coordinates.
     */
    suspend fun teleportToLocation(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        return withContext(Dispatchers.IO) {
            try {
                _teleportState.value = TeleportState.REQUESTING
                _progressMessage.value = "Requesting teleport to $regionName..."
                
                scope.launch {
                    _teleportEvents.emit(TeleportEvent.Started(regionName, x, y, z))
                }
                
                // Use SimulatorLure capability if available
                val lureCap = capabilityManager.getCapability(CapabilityManager.CAP_SIMULATE_LURE)
                if (lureCap != null) {
                    return@withContext teleportViaCapability(regionName, x, y, z)
                }
                
                // Fall back to UDP TeleportLocationRequest
                return@withContext teleportViaUDP(regionName, x, y, z)
            } catch (e: Exception) {
                Log.e(TAG, "Teleport failed", e)
                _teleportState.value = TeleportState.FAILED
                scope.launch {
                    _teleportEvents.emit(TeleportEvent.Failed("Teleport error: ${e.message}"))
                }
                TeleportResult.Failure("Failed to teleport: ${e.message}")
            }
        }
    }
    
    /**
     * Teleport using SLURL format (secondlife://RegionName/x/y/z)
     */
    suspend fun teleportToSLURL(slurl: String): TeleportResult {
        val parsed = parseSLURL(slurl)
        return if (parsed != null) {
            teleportToLocation(parsed.regionName, parsed.x, parsed.y, parsed.z)
        } else {
            TeleportResult.Failure("Invalid SLURL: $slurl")
        }
    }
    
    /**
     * Teleport to a landmark from inventory.
     */
    suspend fun teleportToLandmark(landmarkId: UUID): TeleportResult {
        return withContext(Dispatchers.IO) {
            try {
                _teleportState.value = TeleportState.REQUESTING
                _progressMessage.value = "Teleporting to landmark..."
                
                val payload = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(sessionId)
                
                // Info
                payload.putUUID(landmarkId)
                payload.putInt(TELEPORT_FLAGS_VIA_LANDMARK)
                
                udpConnection.sendPacket(MessageIds.TELEPORT_LANDMARK_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                
                Log.i(TAG, "Sent teleport landmark request for $landmarkId")
                TeleportResult.Pending
            } catch (e: Exception) {
                Log.e(TAG, "Landmark teleport failed", e)
                _teleportState.value = TeleportState.FAILED
                TeleportResult.Failure("Failed to teleport to landmark: ${e.message}")
            }
        }
    }
    
    /**
     * Teleport home.
     */
    suspend fun teleportHome(): TeleportResult {
        return withContext(Dispatchers.IO) {
            try {
                _teleportState.value = TeleportState.REQUESTING
                _progressMessage.value = "Teleporting home..."
                
                scope.launch {
                    _teleportEvents.emit(TeleportEvent.Started("Home", 0f, 0f, 0f))
                }
                
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(sessionId)
                
                // Flags
                payload.putInt(TELEPORT_FLAGS_VIA_HOME)
                
                udpConnection.sendPacket(MessageIds.TELEPORT_HOME_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                
                Log.i(TAG, "Sent teleport home request")
                TeleportResult.Pending
            } catch (e: Exception) {
                Log.e(TAG, "Home teleport failed", e)
                _teleportState.value = TeleportState.FAILED
                TeleportResult.Failure("Failed to teleport home: ${e.message}")
            }
        }
    }
    
    /**
     * Send a teleport offer (lure) to another user.
     */
    suspend fun sendTeleportLure(targetAgentId: UUID, message: String = "Join me!"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                val payload = ByteBuffer.allocate(80 + messageBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(sessionId)
                
                // Info - target agent
                payload.putUUID(targetAgentId)
                
                // Message
                payload.putShort(messageBytes.size.toShort())
                payload.put(messageBytes)
                
                udpConnection.sendPacket(MessageIds.START_LURE, payload.array().copyOf(payload.position()), reliable = true)
                
                Log.i(TAG, "Sent teleport lure to $targetAgentId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send teleport lure", e)
                false
            }
        }
    }
    
    /**
     * Accept a teleport offer.
     */
    suspend fun acceptTeleportLure(lure: TeleportLure): TeleportResult {
        return withContext(Dispatchers.IO) {
            try {
                _teleportState.value = TeleportState.REQUESTING
                _progressMessage.value = "Accepting teleport from ${lure.senderName}..."
                
                val payload = ByteBuffer.allocate(80).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(sessionId)
                
                // Info
                payload.putUUID(lure.senderId)
                payload.putUUID(lure.lureId)
                payload.putInt(TELEPORT_FLAGS_VIA_LURE)
                
                udpConnection.sendPacket(MessageIds.TELEPORT_LURE_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                
                Log.i(TAG, "Accepted teleport lure from ${lure.senderName}")
                pendingLure = null
                TeleportResult.Pending
            } catch (e: Exception) {
                Log.e(TAG, "Failed to accept teleport lure", e)
                _teleportState.value = TeleportState.FAILED
                TeleportResult.Failure("Failed to accept teleport: ${e.message}")
            }
        }
    }
    
    /**
     * Decline a teleport offer.
     */
    fun declineTeleportLure(lure: TeleportLure) {
        // Just clear the pending lure - no message needed
        if (pendingLure?.lureId == lure.lureId) {
            pendingLure = null
        }
        Log.d(TAG, "Declined teleport lure from ${lure.senderName}")
    }
    
    /**
     * Cancel ongoing teleport.
     */
    fun cancelTeleport() {
        if (_teleportState.value == TeleportState.IN_PROGRESS) {
            _teleportState.value = TeleportState.CANCELLED
            _progressMessage.value = "Teleport cancelled"
            scope.launch {
                _teleportEvents.emit(TeleportEvent.Cancelled)
            }
        }
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private suspend fun teleportViaCapability(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        try {
            val request = LLSDMap().apply {
                this["region_name"] = LLSDString(regionName)
                this["position"] = LLSDArray().apply {
                    add(LLSDReal(x.toDouble()))
                    add(LLSDReal(y.toDouble()))
                    add(LLSDReal(z.toDouble()))
                }
                this["look_at"] = LLSDArray().apply {
                    add(LLSDReal(1.0))
                    add(LLSDReal(0.0))
                    add(LLSDReal(0.0))
                }
            }
            
            // TeleportLocation capability
            val response = capabilityManager.request("TeleportLocation", request)
            
            if (response is LLSDMap) {
                val success = response.getBoolean("success") ?: false
                if (success) {
                    _teleportState.value = TeleportState.IN_PROGRESS
                    return TeleportResult.Pending
                } else {
                    val errorMessage = response.getString("message") ?: "Unknown error"
                    _teleportState.value = TeleportState.FAILED
                    return TeleportResult.Failure(errorMessage)
                }
            }
            
            _teleportState.value = TeleportState.FAILED
            return TeleportResult.Failure("Invalid teleport response")
        } catch (e: Exception) {
            Log.e(TAG, "Capability teleport failed", e)
            _teleportState.value = TeleportState.FAILED
            return TeleportResult.Failure("Teleport failed: ${e.message}")
        }
    }
    
    private suspend fun teleportViaUDP(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        try {
            val regionNameBytes = regionName.toByteArray(Charsets.UTF_8)
            val payload = ByteBuffer.allocate(100 + regionNameBytes.size).order(ByteOrder.LITTLE_ENDIAN)
            
            // AgentData - UUIDs use big-endian per SL protocol
            payload.putUUID(agentId)
            payload.putUUID(sessionId)
            
            // Info
            payload.putLong(0) // Region handle (0 = resolve by name)
            payload.putLong(0)
            
            // Position
            payload.putFloat(x)
            payload.putFloat(y)
            payload.putFloat(z)
            
            // Look at
            payload.putFloat(1f)
            payload.putFloat(0f)
            payload.putFloat(0f)
            
            // Flags
            payload.putInt(TELEPORT_FLAGS_VIA_LOCATION)
            
            // Region name
            payload.putShort(regionNameBytes.size.toShort())
            payload.put(regionNameBytes)
            
            udpConnection.sendPacket(MessageIds.TELEPORT_LOCATION_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
            
            _teleportState.value = TeleportState.IN_PROGRESS
            Log.i(TAG, "Sent UDP teleport request to $regionName ($x, $y, $z)")
            return TeleportResult.Pending
        } catch (e: Exception) {
            Log.e(TAG, "UDP teleport failed", e)
            _teleportState.value = TeleportState.FAILED
            return TeleportResult.Failure("Teleport failed: ${e.message}")
        }
    }
    
    // ==================== EVENT HANDLERS ====================
    
    private fun handleTeleportProgress(body: LLSDMap) {
        val message = body.getString("message") ?: "Teleporting..."
        val flags = body.getInt("flags") ?: 0
        
        _progressMessage.value = message
        Log.d(TAG, "Teleport progress: $message (flags=$flags)")
        
        scope.launch {
            _teleportEvents.emit(TeleportEvent.Progress(message))
        }
    }
    
    private fun handleTeleportLocal(body: LLSDMap) {
        Log.d(TAG, "Local teleport complete")
        _teleportState.value = TeleportState.COMPLETED
        _progressMessage.value = "Teleport complete"
        
        scope.launch {
            _teleportEvents.emit(TeleportEvent.Completed("", 0f, 0f, 0f))
        }
    }
    
    private fun handleTeleportFailed(body: LLSDMap) {
        val reason = body.getString("reason") ?: "Unknown reason"
        
        Log.w(TAG, "Teleport failed: $reason")
        _teleportState.value = TeleportState.FAILED
        _progressMessage.value = "Teleport failed: $reason"
        
        scope.launch {
            _teleportEvents.emit(TeleportEvent.Failed(reason))
        }
    }
    
    private fun handleTeleportFinish(body: LLSDMap) {
        val regionName = body.getString("region_name") ?: ""
        
        Log.i(TAG, "Teleport finished to $regionName")
        _teleportState.value = TeleportState.COMPLETED
        _progressMessage.value = "Arrived at $regionName"
        
        scope.launch {
            _teleportEvents.emit(TeleportEvent.Completed(regionName, 0f, 0f, 0f))
        }
    }
    
    private fun handleTeleportStart(body: LLSDMap) {
        Log.d(TAG, "Teleport starting")
        _teleportState.value = TeleportState.IN_PROGRESS
    }
    
    private fun handleEstablishAgentCommunication(body: LLSDMap) {
        // This event is sent when we need to connect to a new sim
        val simHost = body.getString("sim-ip-and-port")
        val seedCap = body.getString("seed-capability")
        
        Log.d(TAG, "Establishing agent communication with $simHost")
        // This would trigger region crossing/teleport completion
    }
    
    // ==================== HELPERS ====================
    
    private fun parseSLURL(slurl: String): SLURLData? {
        // Parse SLURL formats:
        // secondlife://RegionName/x/y/z
        // https://maps.secondlife.com/secondlife/RegionName/x/y/z
        
        val patterns = listOf(
            Regex("secondlife://([^/]+)/?(\\d+)?/?(\\d+)?/?(\\d+)?"),
            Regex("https?://maps\\.secondlife\\.com/secondlife/([^/]+)/?(\\d+)?/?(\\d+)?/?(\\d+)?")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(slurl)
            if (match != null) {
                val regionName = java.net.URLDecoder.decode(match.groupValues[1], Charsets.UTF_8)
                val x = match.groupValues.getOrNull(2)?.toFloatOrNull() ?: 128f
                val y = match.groupValues.getOrNull(3)?.toFloatOrNull() ?: 128f
                val z = match.groupValues.getOrNull(4)?.toFloatOrNull() ?: 25f
                return SLURLData(regionName, x, y, z)
            }
        }
        
        return null
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

// ==================== DATA CLASSES ====================

enum class TeleportState {
    IDLE,
    REQUESTING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}

sealed class TeleportResult {
    object Pending : TeleportResult()
    data class Success(val regionName: String) : TeleportResult()
    data class Failure(val message: String) : TeleportResult()
}

sealed class TeleportEvent {
    data class Started(val regionName: String, val x: Float, val y: Float, val z: Float) : TeleportEvent()
    data class Progress(val message: String) : TeleportEvent()
    data class Completed(val regionName: String, val x: Float, val y: Float, val z: Float) : TeleportEvent()
    data class Failed(val reason: String) : TeleportEvent()
    object Cancelled : TeleportEvent()
    data class LureReceived(val lure: TeleportLure) : TeleportEvent()
}

data class TeleportLure(
    val lureId: UUID,
    val senderId: UUID,
    val senderName: String,
    val regionName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SLURLData(
    val regionName: String,
    val x: Float,
    val y: Float,
    val z: Float
)

/**
 * Extension function to write UUID to ByteBuffer in big-endian (SL protocol format).
 * UUIDs in SL are always stored as 16 raw bytes in big-endian order.
 */
private fun ByteBuffer.putUUID(uuid: UUID): ByteBuffer {
    val originalOrder = order()
    order(ByteOrder.BIG_ENDIAN)
    putLong(uuid.mostSignificantBits)
    putLong(uuid.leastSignificantBits)
    order(originalOrder)
    return this
}
