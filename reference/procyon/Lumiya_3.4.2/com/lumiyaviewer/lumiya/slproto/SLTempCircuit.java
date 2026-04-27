// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.messages.UseCircuitCode;
import java.io.IOException;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.util.List;

public class SLTempCircuit extends SLCircuit
{
    private List<SLMessage> pendingMessages;
    
    public SLTempCircuit(final SLGridConnection slGridConnection, final SLCircuitInfo slCircuitInfo, final SLAuthReply slAuthReply) throws IOException {
        super(slGridConnection, slCircuitInfo, slAuthReply, null);
        this.pendingMessages = new LinkedList<SLMessage>();
    }
    
    @Override
    public void DefaultMessageHandler(final SLMessage slMessage) {
        this.pendingMessages.add(slMessage);
    }
    
    @Override
    public void ProcessNetworkError() {
        this.gridConn.removeTempCircuit(this);
    }
    
    @Override
    public void ProcessTimeout() {
        this.gridConn.removeTempCircuit(this);
    }
    
    public void SendUseCode() {
        final UseCircuitCode useCircuitCode = new UseCircuitCode();
        useCircuitCode.CircuitCode_Field.Code = this.circuitInfo.circuitCode;
        useCircuitCode.CircuitCode_Field.SessionID = this.circuitInfo.sessionID;
        useCircuitCode.CircuitCode_Field.ID = this.circuitInfo.agentID;
        useCircuitCode.isReliable = true;
        this.SendMessage(useCircuitCode);
    }
    
    public List<SLMessage> getPendingMessages() {
        return this.pendingMessages;
    }
}
