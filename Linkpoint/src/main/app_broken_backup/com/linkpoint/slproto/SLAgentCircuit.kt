package com.linkpoint.slproto

import com.linkpoint.Debug
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.caps.SLCapEventQueue
import com.linkpoint.slproto.caps.SLCapEventQueue.CapsEvent
import com.linkpoint.slproto.caps.SLCapEventQueue.CapsEventType
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.messages.SLMessageFactory
import com.linkpoint.slproto.messages.UseCircuitCode
import com.linkpoint.slproto.modules.SLModules
import java.io.IOException

/**
 * Minimal agent circuit implementation providing enough functionality for initial networking.
 */
class SLAgentCircuit(
    private val gridConnection: SLGridConnection,
    circuitInfo: SLCircuitInfo,
    private val authReply: SLAuthReply,
    private val caps: SLCaps,
    tempCircuit: SLTempCircuit?,
) : SLThreadingCircuit(gridConnection, circuitInfo, authReply, tempCircuit), SLCapEventQueue.ICapsEventHandler {

    private val modules: SLModules? = if (!authReply.isTemporary) {
        try {
            SLModules(this, caps, gridConnection)
        } catch (ex: Exception) {
            Debug.Warning(ex)
            null
        }
    } else {
        null
    }

    private val capEventQueue: SLCapEventQueue? = try {
        SLCapEventQueue(caps.getCapabilityOrThrow(SLCaps.SLCapability.EventQueueGet), this)
    } catch (ex: Exception) {
        Debug.Warning(ex)
        null
    }

    init {
        tempCircuit?.GetPendingMessages()?.forEach { message ->
            try {
                handleMessage(message)
            } catch (ex: Exception) {
                Debug.Warning(ex)
            }
        }

        modules?.handleCircuitReady()
    }

    fun getModules(): SLModules? = modules

    fun SendUseCode() {
        val message = UseCircuitCode().apply {
            isReliable = true
            CircuitCode_Field.Code = authReply.circuitCode
            CircuitCode_Field.SessionID = authReply.sessionID
            CircuitCode_Field.ID = authReply.agentID
        }
        sendMessage(message)
    }

    fun SendLogoutRequest() {
        val logout = SLMessageFactory.CreateByID(LOGOUT_MESSAGE_ID)
        logout.isReliable = true
        sendMessage(logout)
    }

    override fun processCloseCircuit() {
        capEventQueue?.stopQueue()
        modules?.handleCloseCircuit()
        super.processCloseCircuit()
    }

    override fun processNetworkError() {
        capEventQueue?.stopQueue()
        super.processNetworkError()
    }

    override fun processTimeout() {
        capEventQueue?.stopQueue()
        super.processTimeout()
    }

    override fun onCapsEvent(event: CapsEvent) {
        when (event.eventType) {
            CapsEventType.TeleportFinish -> gridConnection.notifyLoginSuccess()
            CapsEventType.TeleportFailed -> gridConnection.notifyLoginError("Teleport failed")
            CapsEventType.EstablishAgentCommunication -> handleEstablishAgentCommunication(event)
            else -> Debug.Log("Unhandled CAPS event: ${event.eventType}")
        }
    }

    private fun handleEstablishAgentCommunication(event: CapsEvent) {
        try {
            val body = event.eventBody
            val sim = body.byKey("sim-ip-and-port").asString()
            val seed = body.byKey("seed-capability").asString()
            val agentId = body.byKey("agent-id").asUUID()
            val parts = sim.split(":")
            if (parts.size != 2) return
            val tempReply = SLAuthReply(
                authReply.loginURL,
                authReply.seedCapability,
                agentId,
                parts[0],
                parts[1].toInt(),
                authReply.circuitCode,
                true,
                true,
            )
            gridConnection.addTempCircuit(tempReply)
        } catch (ex: Exception) {
            Debug.Warning(ex)
        }
    }

    companion object {
        private const val LOGOUT_MESSAGE_ID = -9000 // Mapped in SLMessageFactory
    }
}
