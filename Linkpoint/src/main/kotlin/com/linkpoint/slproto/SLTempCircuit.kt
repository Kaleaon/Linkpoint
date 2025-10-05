package com.linkpoint.slproto

import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.messages.UseCircuitCode
import java.io.IOException
import java.util.LinkedList
import java.util.List

class SLTempCircuit : SLCircuit() {
    private List<SLMessage> pendingMessages = LinkedList()

    public SLTempCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, null)
    }

    public Unit DefaultMessageHandler(SLMessage sLMessage) {
        this.pendingMessages.add(sLMessage)
    }

    public Unit ProcessNetworkError() {
        this.gridConn.removeTempCircuit(this)
    }

    public Unit ProcessTimeout() {
        this.gridConn.removeTempCircuit(this)
    }

    public Unit SendUseCode() {
        SLMessage useCircuitCode = UseCircuitCode()
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID
        useCircuitCode.isReliable = true
        SendMessage(useCircuitCode)
    }

    public List<SLMessage> getPendingMessages() {
        return this.pendingMessages
    }
}
