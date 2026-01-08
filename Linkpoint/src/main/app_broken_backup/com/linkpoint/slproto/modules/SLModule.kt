package com.linkpoint.slproto.modules

import com.linkpoint.eventbus.EventBus
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLCircuitInfo
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.SLMessage

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

    fun HandleCircuitReady(): Unit {
    }

    fun HandleCloseCircuit(): Unit {
    }

    fun HandleGlobalOptionsChange(): Unit {
    }

    fun SendMessage(SLMessage sLMessage): Unit {
        this.agentCircuit.SendMessage(sLMessage)
    }

    fun getCircuitInfo(): SLCircuitInfo {
        return this.circuitInfo
    }
}
