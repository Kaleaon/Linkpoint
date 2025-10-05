package com.linkpoint.slproto.modules

import com.linkpoint.eventbus.EventBus
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLCircuitInfo
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.SLMessage

class SLModule {
    /* access modifiers changed from: protected */
    public SLAgentCircuit agentCircuit
    /* access modifiers changed from: protected */
    public SLCircuitInfo circuitInfo
    /* access modifiers changed from: protected */
    val EventBus eventBus = EventBus.getInstance()
    protected SLGridConnection gridConn

    public SLModule(SLAgentCircuit sLAgentCircuit) {
        this.agentCircuit = sLAgentCircuit
        this.circuitInfo = sLAgentCircuit.circuitInfo
        this.gridConn = sLAgentCircuit.getGridConnection()
        sLAgentCircuit.RegisterMessageHandler(this)
    }

    public Unit HandleCircuitReady() {
    }

    public Unit HandleCloseCircuit() {
    }

    public Unit HandleGlobalOptionsChange() {
    }

    public Unit SendMessage(SLMessage sLMessage) {
        this.agentCircuit.SendMessage(sLMessage)
    }

    public SLCircuitInfo getCircuitInfo() {
        return this.circuitInfo
    }
}
