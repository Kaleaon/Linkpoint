package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode
import java.io.IOException
import java.util.LinkedList
import java.util.List

class SLTempCircuit : SLCircuit {
    private var pendingMessages: List<SLMessage> = LinkedList()

    SLTempCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, null)
    }

    fun DefaultMessageHandler(sLMessage: SLMessage): Unit {
        this.pendingMessages.add(sLMessage)
    }

    fun ProcessNetworkError(): Unit {
        this.gridConn.removeTempCircuit(this)
    }

    fun ProcessTimeout(): Unit {
        this.gridConn.removeTempCircuit(this)
    }

    fun SendUseCode(): Unit {
        SLMessage useCircuitCode = UseCircuitCode()
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID
        useCircuitCode.isReliable = true
        SendMessage(useCircuitCode)
    }

    fun getPendingMessages(): List<SLMessage> {
        return this.pendingMessages
    }
}
