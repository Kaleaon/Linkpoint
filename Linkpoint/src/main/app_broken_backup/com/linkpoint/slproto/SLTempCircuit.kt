package com.linkpoint.slproto

import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.messages.UseCircuitCode

/**
 * Temporary circuit used while establishing a full agent connection.
 */
class SLTempCircuit @JvmOverloads constructor(
    gridConnection: SLGridConnection,
    circuitInfo: SLCircuitInfo,
    authReply: SLAuthReply,
    previousCircuit: SLCircuit? = null,
) : SLCircuit(gridConnection, circuitInfo, authReply, previousCircuit) {

    private val pendingMessages = mutableListOf<SLMessage>()

    override fun defaultMessageHandler(message: SLMessage) {
        pendingMessages += message
    }

    override fun processNetworkError() {
        super.processNetworkError()
        gridConn.removeTempCircuit(this)
    }

    override fun processTimeout() {
        super.processTimeout()
        gridConn.removeTempCircuit(this)
    }

    fun sendUseCode() {
        val message = UseCircuitCode().apply {
            isReliable = true
            CircuitCode_Field.Code = circuitInfo.circuitCode
            CircuitCode_Field.SessionID = circuitInfo.sessionID
            CircuitCode_Field.ID = circuitInfo.agentID
        }
        sendMessage(message)
    }

    fun getPendingMessages(): List<SLMessage> = pendingMessages

    // Legacy aliases ------------------------------------------------------

    fun SendUseCode() = sendUseCode()

    fun GetPendingMessages(): List<SLMessage> = getPendingMessages()
}
