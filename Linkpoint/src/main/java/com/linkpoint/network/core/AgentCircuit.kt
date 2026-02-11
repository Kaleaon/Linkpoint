package com.linkpoint.network.core

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.CircuitDispatcher
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.MessageRouter
import com.linkpoint.protocol.messages.MessageEventListener
import com.linkpoint.protocol.scenery.SceneDataHandler
import com.linkpoint.render.RenderQueue
import com.linkpoint.render.SceneGraph
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

/**
 * Agent Circuit with Circuit Establishment State Machine
 *
 * Following Lumiya's architecture: this circuit does NOT create its own UDP socket.
 * Instead, it shares the main app's UDPConnectionFixed, registering message handlers
 * for scene data (rendering) while the main connection handles communications.
 *
 * This follows Lumiya's SLAgentCircuit pattern where a single SLConnection (IO thread)
 * manages all circuits through a shared NIO Selector.
 *
 * Circuit establishment sequence:
 * 1. Register scene handlers on shared connection
 * 2. Circuit ready - scene data flows through shared socket
 * 3. Agent updates sent through shared socket
 */
class AgentCircuit(
    private val authReply: AuthReply,
    private val sharedConnection: UDPConnectionFixed? = null,
    private val sceneGraph: SceneGraph? = null,
    private val renderQueue: RenderQueue? = null,
    private val scope: CoroutineScope = CoroutineScope(CircuitDispatcher.dispatcher + SupervisorJob())
) {

    companion object {
        private const val TAG = "AgentCircuit"
        private const val AGENT_UPDATE_INTERVAL_MS = 100L
    }

    private val _circuitState = MutableStateFlow(CircuitState.DISCONNECTED)
    val circuitState: StateFlow<CircuitState> = _circuitState.asStateFlow()

    private var isConnected: Boolean = false
    private var agentUpdateJob: Job? = null
    private var stateListener: CircuitStateListener? = null

    /**
     * Use the shared connection if provided (correct Lumiya-style architecture).
     * Only fall back to creating own connection if no shared connection given
     * (backwards compatibility during migration).
     */
    private val udpConnection: UDPConnectionFixed = sharedConnection ?: UDPConnectionFixed(
        simIP = authReply.simIP,
        simPort = authReply.simPort,
        circuitCode = authReply.circuitCode
    ).apply {
        setSessionInfo(authReply.sessionId, authReply.agentId)
    }

    private val usesSharedConnection = sharedConnection != null

    private val messageRouter = udpConnection.getMessageRouter()
    private val sceneDataHandler = SceneDataHandler(sceneGraph, renderQueue)

    interface CircuitStateListener {
        fun onStateChanged(from: CircuitState, to: CircuitState)
        fun onCircuitReady()
        fun onCircuitError(reason: String)
    }

    init {
        scope.launch { initializeCircuit() }
    }

    fun setStateListener(listener: CircuitStateListener) {
        this.stateListener = listener
    }

    private fun transitionState(newState: CircuitState, reason: String = "") {
        val oldState = _circuitState.value
        if (oldState != newState) {
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "Circuit state: $oldState → $newState ($reason)")
            _circuitState.value = newState
            stateListener?.onStateChanged(oldState, newState)
            if (newState == CircuitState.CIRCUIT_READY) {
                stateListener?.onCircuitReady()
            }
        }
    }

    private suspend fun initializeCircuit() {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "=== Initializing Agent Circuit ===")
        try {
            registerSceneDataHandlers()
            establishCircuit()
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Failed: ${e.message}")
            transitionState(CircuitState.ERROR, "Initialization failed")
            stateListener?.onCircuitError("Initialization failed: ${e.message}")
            throw e
        }
    }

    private suspend fun establishCircuit() {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "=== Starting Circuit Establishment ===")
        transitionState(CircuitState.CONNECTING, "Starting connection")

        if (usesSharedConnection) {
            // Shared connection is already connected - skip socket creation
            // The main app's SecondLifeProtocol already sent UseCircuitCode and CompleteAgentMovement
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "Using shared connection (Lumiya-style) - skipping redundant UseCircuitCode")
            isConnected = true
            transitionState(CircuitState.USE_CIRCUIT_CODE_ACKED, "Shared connection")
            transitionState(CircuitState.COMPLETE_AGENT_MOVEMENT_ACKED, "Shared connection")
            transitionState(CircuitState.CIRCUIT_READY, "Ready (shared connection)")
            startAgentUpdates()
        } else {
            // Legacy path: own connection (will be removed in future)
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                "WARNING: AgentCircuit creating own socket - this creates competing connections!")
            val connected = udpConnection.connect()
            if (!connected) {
                transitionState(CircuitState.ERROR, "UDP connection failed")
                stateListener?.onCircuitError("UDP connection failed")
                return
            }
            isConnected = true
            sendUseCircuitCode()
        }
    }

    private suspend fun sendUseCircuitCode() {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "Sending UseCircuitCode")

        val listener = object : MessageEventListener {
            override fun onMessageAcknowledged(seqNum: Int, msgId: Int) {
                NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "UseCircuitCode ACKed")
                transitionState(CircuitState.USE_CIRCUIT_CODE_ACKED, "ACKed")
                scope.launch { sendCompleteAgentMovement() }
            }

            override fun onMessageTimeout(seqNum: Int, msgId: Int) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "UseCircuitCode timeout")
                transitionState(CircuitState.ERROR, "Timeout")
                stateListener?.onCircuitError("UseCircuitCode timeout")
            }
        }

        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.putInt(authReply.circuitCode)
        payload.put(authReply.sessionId.asBytes())
        payload.put(authReply.agentId.asBytes())

        udpConnection.sendPacket(MessageIds.USE_CIRCUIT_CODE, payload.array(), reliable = true, listener = listener)
        transitionState(CircuitState.USE_CIRCUIT_CODE_SENT, "Sent")
    }

    private suspend fun sendCompleteAgentMovement() {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "Sending CompleteAgentMovement")

        val listener = object : MessageEventListener {
            override fun onMessageAcknowledged(seqNum: Int, msgId: Int) {
                NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "CompleteAgentMovement ACKed - CIRCUIT READY")
                transitionState(CircuitState.COMPLETE_AGENT_MOVEMENT_ACKED, "ACKed")
                transitionState(CircuitState.CIRCUIT_READY, "Ready")
                scope.launch { startAgentUpdates() }
            }

            override fun onMessageTimeout(seqNum: Int, msgId: Int) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "CompleteAgentMovement timeout")
                transitionState(CircuitState.ERROR, "Timeout")
                stateListener?.onCircuitError("CompleteAgentMovement timeout")
            }
        }

        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.put(authReply.agentId.asBytes())
        payload.put(authReply.sessionId.asBytes())
        payload.putInt(authReply.circuitCode)

        udpConnection.sendPacket(MessageIds.COMPLETE_AGENT_MOVEMENT, payload.array(), reliable = true, listener = listener)
        transitionState(CircuitState.COMPLETE_AGENT_MOVEMENT_SENT, "Sent")
    }

    private suspend fun registerSceneDataHandlers() {
        messageRouter.registerHandler(MessageIds.LAYER_DATA, object : MessageRouter.Handler {
            override fun handleMessage(msgId: Int, data: ByteArray) = sceneDataHandler.handleLayerData(data)
            override fun getPriority() = 0
        })

        messageRouter.registerHandler(MessageIds.OBJECT_UPDATE, object : MessageRouter.Handler {
            override fun handleMessage(msgId: Int, data: ByteArray) = sceneDataHandler.handleObjectUpdate(data)
            override fun getPriority() = 0
        })

        messageRouter.registerHandler(MessageIds.OBJECT_PROPERTIES, object : MessageRouter.Handler {
            override fun handleMessage(msgId: Int, data: ByteArray) = sceneDataHandler.handleObjectProperties(data)
            override fun getPriority() = 0
        })

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Scene handlers registered on ${if (usesSharedConnection) "shared" else "own"} connection")
    }

    private fun startAgentUpdates() {
        agentUpdateJob = scope.launch {
            while (isActive && isConnected) {
                try {
                    udpConnection.sendAgentUpdate()
                    delay(AGENT_UPDATE_INTERVAL_MS)
                } catch (e: Exception) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Agent update error: ${e.message}")
                }
            }
        }
    }

    suspend fun registerHandler(msgId: Int, handler: MessageRouter.Handler) {
        messageRouter.registerHandler(msgId, handler)
    }

    suspend fun sendMessage(msgId: Int, payload: ByteArray, reliable: Boolean = false) {
        if (!isConnected) return
        try {
            udpConnection.sendPacket(msgId, payload, reliable)
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Send error: ${e.message}")
        }
    }

    fun getStatistics() = mapOf(
        "state" to _circuitState.value.name,
        "connected" to isConnected,
        "usesSharedConnection" to usesSharedConnection,
        "sceneStats" to sceneDataHandler.getStatistics()
    )

    fun isCircuitReady() = _circuitState.value == CircuitState.CIRCUIT_READY

    fun close() {
        isConnected = false
        agentUpdateJob?.cancel()
        try {
            // Only disconnect if we own the connection
            if (!usesSharedConnection) {
                udpConnection.disconnect()
            }
            scope.launch {
                EventBus.publish(ConnectionStateChangedEvent(ConnectionState.CONNECTED, ConnectionState.DISCONNECTED))
            }
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Close error: ${e.message}")
        }
        transitionState(CircuitState.DISCONNECTED, "Closed")
        scope.cancel()
    }

    private fun UUID.asBytes(): ByteArray {
        val bytes = ByteArray(16)
        val msb = this.mostSignificantBits
        val lsb = this.leastSignificantBits
        for (i in 7 downTo 0) bytes[7 - i] = (msb shr (i * 8)).toByte()
        for (i in 7 downTo 0) bytes[15 - i] = (lsb shr (i * 8)).toByte()
        return bytes
    }
}
