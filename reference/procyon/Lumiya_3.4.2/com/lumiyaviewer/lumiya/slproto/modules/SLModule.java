// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;

public class SLModule
{
    protected SLAgentCircuit agentCircuit;
    protected SLCircuitInfo circuitInfo;
    protected final EventBus eventBus;
    protected SLGridConnection gridConn;
    
    public SLModule(final SLAgentCircuit agentCircuit) {
        this.eventBus = EventBus.getInstance();
        this.agentCircuit = agentCircuit;
        this.circuitInfo = agentCircuit.circuitInfo;
        this.gridConn = agentCircuit.getGridConnection();
        agentCircuit.RegisterMessageHandler(this);
    }
    
    public void HandleCircuitReady() {
    }
    
    public void HandleCloseCircuit() {
    }
    
    public void HandleGlobalOptionsChange() {
    }
    
    public void SendMessage(final SLMessage slMessage) {
        this.agentCircuit.SendMessage(slMessage);
    }
    
    public SLCircuitInfo getCircuitInfo() {
        return this.circuitInfo;
    }
}
