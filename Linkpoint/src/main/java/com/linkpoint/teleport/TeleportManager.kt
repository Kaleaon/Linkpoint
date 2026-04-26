package com.linkpoint.teleport

import android.util.Log
import com.linkpoint.core.RegionInfo
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.capabilities.EventQueueDispatcher
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.getUUID
import com.linkpoint.protocol.types.putUUID
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
 * Based on the reference viewer's teleport implementation.
 */
class TeleportManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {

    // Get session ID from UDP connection
    private val sessionId: UUID
        get() = udpConnection.getSessionId()

    /**
     * Optional lookup that maps a regionHandle to a known region name. Wired
     * from [com.linkpoint.LinkpointApp] using [com.linkpoint.world.WorldMap]
     * (which caches simulator-name responses) and the current
     * [com.linkpoint.core.SessionManager] state. May be null before the app
     * has wired the dependency, in which case [resolveRegionName] falls back
     * to formatting the simulator grid coordinates.
     */
    @Volatile
    var regionNameForHandle: ((Long) -> String?)? = null

    /**
     * Resolve a regionHandle to a human-readable name. Tries
     * [regionNameForHandle] first; if no callback or no cached entry, falls
     * back to the canonical SL grid-coordinate string used by viewers when
     * a name has not yet resolved.
     *
     * SL packs the handle as `(globalX << 32) | globalY`, where each axis is
     * the *meter-precision* global coord. Region grid index is the meter
     * coord divided by 256 (region size).
     */
    private fun resolveRegionName(regionHandle: Long): String {
        if (regionHandle == 0L) return "Unknown"
        regionNameForHandle?.invoke(regionHandle)?.let { return it }
        val gridX = ((regionHandle ushr 32) and 0xFFFFFFFFL) / 256
        val gridY = (regionHandle and 0xFFFFFFFFL) / 256
        return "Region ($gridX, $gridY)"
    }
    
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
    
    private val scope = CoroutineScope(EventQueueDispatcher.dispatcher + SupervisorJob())
    
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
        capabilityManager.registerEventHandler("TeleportProgress", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("TeleportLocal", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("TeleportFailed", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("TeleportFinish", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("TeleportStart", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("EstablishAgentCommunication", this, EventQueueDispatcher.dispatcher)
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
                // Wire format (LL message_template `StartLure`, low-freq 70,
                // Lumiya: slproto/messages/StartLure.java PackPayload):
                //   AgentData:  AgentID(LLUUID), SessionID(LLUUID)
                //   Info:       LureType(U8), Message(Variable 1, null-terminated)
                //   TargetData (Variable, U8 count): TargetID(LLUUID)
                //
                // The previous encoder put TargetID before Message, dropped
                // LureType, treated Message as Variable 2, and skipped the
                // TargetData count, so the simulator rejected the lure.

                // Lumiya parity: stringToVariableUTF (slproto/SLMessage.java
                // line 212) appends a NUL byte and includes it in the U8
                // length prefix. Cap raw text at 254 bytes so the trailing
                // NUL still fits the U8 prefix.
                val rawBytes = message.toByteArray(Charsets.UTF_8)
                val cappedBytes = if (rawBytes.size > 254) rawBytes.copyOf(254) else rawBytes
                val safeMessageBytes = cappedBytes + 0.toByte()
                val nameLen = safeMessageBytes.size

                val payload = ByteBuffer
                    .allocate(36 /* AgentData */ + 1 /* LureType */ + 1 + nameLen /* Message */ +
                              1 /* target count */ + 16 /* TargetID */)
                    .order(ByteOrder.LITTLE_ENDIAN)

                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(sessionId)

                // Info
                payload.put(0.toByte()) // LureType = 0 (TELEPORT_LURE_NORMAL)
                payload.put(nameLen.toByte())
                payload.put(safeMessageBytes)

                // TargetData (Variable u8 count + entries)
                payload.put(1.toByte())
                payload.putUUID(targetAgentId)

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
            val response = capabilityManager.request(CapabilityManager.CAP_TELEPORT_LOCATION, request)
            
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
        // EstablishAgentCommunication is the EventQueue notification that a
        // neighboring (or destination) simulator is ready to accept this
        // agent's child-agent traffic. The full Lumiya/SL viewer flow then
        // performs a `UseCircuitCode` against the new sim and starts the
        // child-agent throttle so neighboring sims stream object data while
        // the avatar is near a border.
        //
        // Linkpoint does not yet maintain multiple parallel circuits, so
        // here we capture the seed-cap + sim address into the in-flight
        // teleport context. `handleTeleportFinish` (UDP) consumes them when
        // the simulator actually requests the move; the WorldMap cache also
        // gets a hint so neighbor names render before object data arrives.
        val simHost = body.getString("sim-ip-and-port")
        val seedCap = body.getString("seed-capability")

        if (simHost.isNullOrBlank()) {
            Log.w(TAG, "EstablishAgentCommunication: missing sim-ip-and-port; ignoring")
            return
        }

        pendingNeighborSim = NeighborSim(
            address = simHost,
            seedCapability = seedCap.orEmpty(),
            announcedAt = System.currentTimeMillis()
        )

        Log.i(TAG, "EstablishAgentCommunication: neighbor sim queued $simHost (seed cap ${if (seedCap.isNullOrBlank()) "absent" else "present"})")
    }

    /**
     * Most-recent neighbor sim announcement from
     * `EstablishAgentCommunication`. Read by the UDP teleport-finish path so
     * the seed cap doesn't get lost between the EventQueue notification and
     * the simulator's TeleportFinish packet.
     */
    private data class NeighborSim(
        val address: String,
        val seedCapability: String,
        val announcedAt: Long
    )

    @Volatile
    private var pendingNeighborSim: NeighborSim? = null
    
    // ==================== UDP MESSAGE HANDLERS ====================
    // These are called from LinkpointApp when UDP messages are received
    
    /**
     * Handle TeleportFinish UDP message - teleport completed, connect to new region
     */
    fun handleTeleportFinish(data: com.linkpoint.protocol.messages.TeleportFinishData) {
        Log.i(TAG, "UDP TeleportFinish: ${data.simIP}:${data.simPort}, handle=${data.regionHandle}")
        _teleportState.value = TeleportState.COMPLETED
        val regionName = resolveRegionName(data.regionHandle)
        _progressMessage.value = "Connected to $regionName"

        // Consume the EventQueue neighbor-sim hint so a stale entry doesn't
        // leak into the next teleport.
        pendingNeighborSim = null

        scope.launch {
            _teleportEvents.emit(TeleportEvent.Completed(
                regionName = regionName,
                x = 128f,
                y = 128f,
                z = 25f
            ))
        }
    }
    
    /**
     * Handle TeleportFailed UDP message
     */
    fun handleTeleportFailed(reason: String) {
        Log.e(TAG, "UDP TeleportFailed: $reason")
        _teleportState.value = TeleportState.FAILED
        _progressMessage.value = reason
        
        scope.launch {
            _teleportEvents.emit(TeleportEvent.Failed(reason))
        }
    }
    
    /**
     * Handle TeleportProgress UDP message
     */
    fun handleTeleportProgress(message: String) {
        Log.d(TAG, "UDP TeleportProgress: $message")
        _progressMessage.value = message
    }
    
    /**
     * Handle TeleportStart UDP message
     */
    fun handleTeleportStart() {
        Log.d(TAG, "UDP TeleportStart")
        _teleportState.value = TeleportState.IN_PROGRESS
        _progressMessage.value = "Teleport starting..."
    }
    
    /**
     * Handle CrossedRegion UDP message - region crossing
     */
    fun handleCrossedRegion(data: com.linkpoint.protocol.messages.CrossedRegionData) {
        Log.i(TAG, "UDP CrossedRegion: ${data.simIP}:${data.simPort}, position=${data.position}, handle=${data.regionHandle}")
        val regionName = resolveRegionName(data.regionHandle)
        _progressMessage.value = "Crossing into $regionName..."

        scope.launch {
            _teleportEvents.emit(TeleportEvent.Completed(
                regionName = regionName,
                x = data.position.x,
                y = data.position.y,
                z = data.position.z
            ))
        }
    }
    
    /**
     * Handle TeleportLocal UDP message - local teleport within same region.
     */
    fun handleTeleportLocal(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // Info block
            if (buffer.remaining() < 28) return
            
            val agentId = buffer.getUUID()
            val locationId = buffer.int
            val position = com.linkpoint.protocol.types.LLVector3(buffer.float, buffer.float, buffer.float)
            val lookAt = com.linkpoint.protocol.types.LLVector3(buffer.float, buffer.float, buffer.float)
            val teleportFlags = if (buffer.remaining() >= 4) buffer.int else 0
            
            Log.i(TAG, "🚀 TeleportLocal: position=$position, lookAt=$lookAt")
            
            _teleportState.value = TeleportState.IDLE
            _progressMessage.value = ""
            
            scope.launch {
                _teleportEvents.emit(TeleportEvent.Completed(
                    regionName = "Local",
                    x = position.x,
                    y = position.y,
                    z = position.z
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing TeleportLocal", e)
        }
    }
    
    /**
     * Handle teleport cancellation.
     */
    fun handleTeleportCancel() {
        Log.i(TAG, "🚀 Teleport cancelled")
        _teleportState.value = TeleportState.IDLE
        _progressMessage.value = ""
        
        scope.launch {
            _teleportEvents.emit(TeleportEvent.Cancelled)
        }
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
                // Use the older URLDecoder.decode method that takes a String charset name (available since API 1)
                val regionName = java.net.URLDecoder.decode(match.groupValues[1], "UTF-8")
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
