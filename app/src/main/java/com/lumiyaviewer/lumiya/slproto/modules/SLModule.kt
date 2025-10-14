package com.lumiyaviewer.lumiya.slproto.modules

import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.SLMessage

class SLModule {
    /* access modifiers changed from: protected */
    SLAgentCircuit agentCircuit
    /* access modifiers changed from: protected */
    SLCircuitInfo circuitInfo
    /* access modifiers changed from: protected */
    EventBus eventBus = EventBus.getInstance()
    protected SLGridConnection gridConn

    SLModule(SLAgentCircuit sLAgentCircuit) {
        this.agentCircuit = sLAgentCircuit
        this.circuitInfo = sLAgentCircuit.circuitInfo
        this.gridConn = sLAgentCircuit.getGridConnection()
        sLAgentCircuit.RegisterMessageHandler(this)
    }

    Unit HandleCircuitReady() {
    }

    Unit HandleCloseCircuit() {
    }

    Unit HandleGlobalOptionsChange() {
    }

    Unit SendMessage(SLMessage sLMessage) {
        this.agentCircuit.SendMessage(sLMessage)
    }

    SLCircuitInfo getCircuitInfo() {
        return this.circuitInfo
    }
}
