package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEvent
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageFactory
import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import java.io.IOException
import java.util.UUID

/**
 * Minimal agent circuit implementation providing enough functionality for initial networking.
 */
class SLAgentCircuit(
    private val gridConnection: SLGridConnection,
    circuitInfo: SLCircuitInfo,
    paramAuthReply: SLAuthReply, // Renamed to avoid conflict
    private val caps: SLCaps,
    tempCircuit: SLTempCircuit?,
) : SLThreadingCircuit(gridConnection, circuitInfo, paramAuthReply, tempCircuit), SLCapEventQueue.ICapsEventHandler {

    private val modules: SLModules? = if (!paramAuthReply.isTemporary) {
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
            CircuitCode_Field.Code = authReply.circuitCode // Accesses inherited property
            CircuitCode_Field.SessionID = authReply.sessionID ?: UUID(0,0)
            CircuitCode_Field.ID = authReply.agentID ?: UUID(0,0)
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
            val agentId = body.byKey("agent-id").asUUID()
            val parts = sim.split(":")
            if (parts.size != 2) return
            
            val tempReply = SLAuthReply(
                authReply, // Access inherited property
                true, // fromTeleport
                true, // isTemporary
                agentId,
                parts[0],
                parts[1].toInt(),
                authReply.seedCapability
            )
            gridConnection.addTempCircuit(tempReply)
        } catch (ex: Exception) {
            Debug.Warning(ex)
        }
    }

    companion object {
        private const val LOGOUT_MESSAGE_ID = -9000
    }
}
