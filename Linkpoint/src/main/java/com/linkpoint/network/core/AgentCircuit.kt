package com.linkpoint.network.core

import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.messages.CircuitDispatcher
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.MessageRouter
import com.linkpoint.protocol.scenery.SceneDataHandler
import com.linkpoint.render.RenderQueue
import com.linkpoint.render.SceneGraph
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val sharedConnection: UDPConnectionFixed,
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

    private val udpConnection: UDPConnectionFixed = sharedConnection
    private val lifecycleOwnerId = "AgentCircuit:${authReply.agentId}:${authReply.circuitCode}"

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
        NetworkLogger.log(
            NetworkLogger.Level.INFO,
            NetworkLogger.Category.UDP,
            "Using shared connection (Lumiya-style) - skipping redundant UseCircuitCode"
        )
        isConnected = true
        transitionState(CircuitState.USE_CIRCUIT_CODE_ACKED, "Shared connection")
        transitionState(CircuitState.COMPLETE_AGENT_MOVEMENT_ACKED, "Shared connection")
        transitionState(CircuitState.CIRCUIT_READY, "Ready (shared connection)")
        startAgentUpdates()
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

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Scene handlers registered on shared connection")
    }

    private fun startAgentUpdates() {
        val lifecycleAcquired = udpConnection.tryAcquireMovementLifecycle(lifecycleOwnerId)
        if (!lifecycleAcquired) {
            val currentOwner = udpConnection.getMovementLifecycleOwner()
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "Skipping AgentCircuit update loop; movement lifecycle already owned by $currentOwner"
            )
            return
        }

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
        "usesSharedConnection" to true,
        "sceneStats" to sceneDataHandler.getStatistics()
    )

    fun isCircuitReady() = _circuitState.value == CircuitState.CIRCUIT_READY

    fun close() {
        isConnected = false
        agentUpdateJob?.cancel()
        udpConnection.releaseMovementLifecycle(lifecycleOwnerId)
        try {
            scope.launch {
                EventBus.publish(ConnectionStateChangedEvent(ConnectionState.CONNECTED, ConnectionState.DISCONNECTED))
            }
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Close error: ${e.message}")
        }
        transitionState(CircuitState.DISCONNECTED, "Closed")
        scope.cancel()
    }

}
