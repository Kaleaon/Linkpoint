package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SLModule {
    /* access modifiers changed from: protected */
    public SLAgentCircuit agentCircuit;
    /* access modifiers changed from: protected */
    public SLCircuitInfo circuitInfo;
    /* access modifiers changed from: protected */
    public final EventBus eventBus = EventBus.getInstance();
    protected SLGridConnection gridConn;

    public SLModule(SLAgentCircuit sLAgentCircuit) {
        this.agentCircuit = sLAgentCircuit;
        this.circuitInfo = sLAgentCircuit.circuitInfo;
        this.gridConn = sLAgentCircuit.getGridConnection();
        sLAgentCircuit.RegisterMessageHandler(this);
    }

    public void HandleCircuitReady() {
    }

    public void HandleCloseCircuit() {
    }

    public void HandleGlobalOptionsChange() {
    }

    public void SendMessage(SLMessage sLMessage) {
        this.agentCircuit.SendMessage(sLMessage);
    }

    public SLCircuitInfo getCircuitInfo() {
        return this.circuitInfo;
    }
}
