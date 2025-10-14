package com.lumiyaviewer.lumiya.slproto.modules

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit

class SLDrawDistance : SLModule {
    Float CHAT_RANGE = 20.0f
    private Long DRAW_RANGE_TIMEOUT = 10000
    Float MIN_DRAW_RANGE = 10.5f
    private Float activeDrawDistance = 0.0f
    private Long defaultDrawDistanceSince = 0
    private Boolean defaultTimerSet = false
    private volatile Boolean keepDrawDistance = false
    private Float keepSelectDistance = 20.0f
    private Float objectSelectDistance = 20.0f
    private volatile Boolean objectSelectionActive = false
    private Float wantedDrawDistance = 10.5f
    private Float worldDrawDistance = 20.0f
    private volatile Boolean worldViewActive = false

    SLDrawDistance(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
    }

    private synchronized Unit updateWantedDrawDistance() {
        Boolean z = true
        synchronized (this) {
            Float f = 10.5f
            if (this.worldViewActive) {
                f = Math.max(10.5f, this.worldDrawDistance)
            }
            if (this.objectSelectionActive) {
                f = Math.max(f, this.objectSelectDistance)
            }
            if (this.keepDrawDistance) {
                f = Math.max(f, this.keepSelectDistance)
            }
            if (!this.worldViewActive && !this.objectSelectionActive) {
                z = this.keepDrawDistance
            }
            if (!z) {
                if (!this.defaultTimerSet) {
                    this.defaultDrawDistanceSince = System.currentTimeMillis()
                    this.defaultTimerSet = true
                }
                if (f != this.wantedDrawDistance && System.currentTimeMillis() < this.defaultDrawDistanceSince + DRAW_RANGE_TIMEOUT) {
                    f = this.wantedDrawDistance
                }
            } else {
                this.defaultTimerSet = false
            }
            this.wantedDrawDistance = f
        }
    }

    synchronized Unit Disable3DView() {
        Debug.Log("DrawDistance: Disable 3D View.")
        if (this.worldViewActive) {
            this.worldViewActive = false
            this.agentCircuit.getModules().avatarControl.DisableFastUpdates()
        }
        this.agentCircuit.TryWakeUp()
    }

    synchronized Unit DisableKeepDistance() {
        this.keepDrawDistance = false
    }

    synchronized Unit DisableObjectSelect() {
        this.objectSelectionActive = false
        this.agentCircuit.TryWakeUp()
    }

    synchronized Unit Enable3DView(Int i) {
        Debug.Log("Enable3DView: Setting drawDistance to " + i)
        this.worldDrawDistance = (Float) i
        if (!this.worldViewActive) {
            this.worldViewActive = true
            this.agentCircuit.getModules().avatarControl.EnableFastUpdates()
        }
        this.gridConn.parcelInfo.setDrawDistance((Float) i)
        this.agentCircuit.TryWakeUp()
    }

    synchronized Unit EnableKeepDistance(Float f) {
        this.keepDrawDistance = true
        this.keepSelectDistance = f
    }

    synchronized Unit EnableObjectSelect() {
        this.objectSelectionActive = true
        this.agentCircuit.TryWakeUp()
    }

    synchronized Float getDrawDistanceForUpdate() {
        updateWantedDrawDistance()
        this.activeDrawDistance = this.wantedDrawDistance
        return this.activeDrawDistance
    }

    synchronized Float getObjectSelectRange() {
        return this.objectSelectDistance
    }

    synchronized Boolean is3DViewEnabled() {
        return this.worldViewActive
    }

    synchronized Boolean isObjectSelectEnabled() {
        return this.objectSelectionActive
    }

    synchronized Boolean needUpdateDrawDistance() {
        updateWantedDrawDistance()
        return this.wantedDrawDistance != this.activeDrawDistance
    }

    synchronized Unit setObjectSelectRange(Float f) {
        this.objectSelectDistance = f
        this.agentCircuit.TryWakeUp()
    }
}
