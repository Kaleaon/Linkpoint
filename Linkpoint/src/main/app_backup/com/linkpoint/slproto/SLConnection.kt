package com.linkpoint.slproto

import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.messages.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.util.UUID

/**
 * Handles authentication and UDP circuit management for Second Life connections.
 */
class SLConnection(
    private val authClient: SLAuth = SLAuth(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    enum class State {
        DISCONNECTED,
        AUTHENTICATING,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
    }

    private var circuit: SLCircuit? = null
    private var authReply: SLAuthReply? = null
    private var state: State = State.DISCONNECTED

    var onStateChanged: ((State) -> Unit)? = null
    var onMessageReceived: ((SLMessage) -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null

    suspend fun connect(params: SLAuthParams): Boolean =
        withContext(dispatcher) {
            try {
                setState(State.AUTHENTICATING)
                val reply = authClient.sendLoginRequest(params)
                authReply = reply

                if (!reply.success) {
                    val reason = reply.reason ?: reply.message ?: "Unknown error"
                    throw IllegalStateException("Authentication failed: $reason")
                }

                val simIp = reply.simIp ?: throw IllegalStateException("Auth reply missing sim_ip")
                val simPort = reply.simPort ?: throw IllegalStateException("Auth reply missing sim_port")
                val circuitCode = reply.circuitCode ?: throw IllegalStateException("Auth reply missing circuit_code")
                val agentId = UUID.fromString(reply.agentId ?: throw IllegalStateException("Auth reply missing agent_id"))
                val sessionId = UUID.fromString(reply.sessionId ?: throw IllegalStateException("Auth reply missing session_id"))

                val address = InetAddress.getByName(simIp)
                val newCircuit = SLCircuit(address, simPort).apply {
                    this.circuitCode = circuitCode
                    this.agentId = agentId
                    this.sessionId = sessionId
                    onMessageReceived = { handleMessage(it) }
                    onCircuitClosed = { setState(State.DISCONNECTED) }
                }

                circuit = newCircuit
                setState(State.CONNECTING)
                newCircuit.start()

                sendUseCircuitCode(newCircuit, circuitCode, sessionId, agentId)
                sendCompleteAgentMovement(newCircuit, agentId, sessionId, circuitCode)

                setState(State.CONNECTED)
                true
            } catch (t: Throwable) {
                onError?.invoke(t)
                if (circuit != null) {
                    disconnect()
                } else {
                    setState(State.DISCONNECTED)
                }
                false
            }
        }

    fun disconnect() {
        setState(State.DISCONNECTING)
        circuit?.stop()
        circuit = null
        setState(State.DISCONNECTED)
    }

    fun sendMessage(message: SLMessage) {
        circuit?.sendMessage(message) ?: onError?.invoke(IllegalStateException("Circuit not established"))
    }

    fun getState(): State = state

    fun getAuthReply(): SLAuthReply? = authReply

    private fun handleMessage(message: SLMessage) {
        when (message) {
            is RegionHandshakeMessage -> handleRegionHandshake(message)
            else -> onMessageReceived?.invoke(message)
        }
    }

    private fun handleRegionHandshake(message: RegionHandshakeMessage) {
        val reply = RegionHandshakeReplyMessage().apply {
            agentId = circuit?.agentId ?: UUID.randomUUID()
            sessionId = circuit?.sessionId ?: UUID.randomUUID()
            flags = 0
            isReliable = true
        }
        sendMessage(reply)
        onMessageReceived?.invoke(message)
    }

    private fun sendUseCircuitCode(
        circuit: SLCircuit,
        circuitCode: Int,
        sessionId: UUID,
        agentId: UUID,
    ) {
        val message = UseCircuitCodeMessage().apply {
            this.circuitCode = circuitCode
            this.sessionId = sessionId
            this.agentId = agentId
            isReliable = true
        }
        circuit.sendMessage(message)
    }

    private fun sendCompleteAgentMovement(
        circuit: SLCircuit,
        agentId: UUID,
        sessionId: UUID,
        circuitCode: Int,
    ) {
        val message = CompleteAgentMovementMessage().apply {
            this.agentId = agentId
            this.sessionId = sessionId
            this.circuitCode = circuitCode
            isReliable = true
        }
        circuit.sendMessage(message)
    }

    private fun setState(newState: State) {
        if (state == newState) return
        state = newState
        onStateChanged?.invoke(newState)
    }
}
