package com.linkpoint.slproto.modules

import kotlin.math.*

import com.linkpoint.Debug
import com.linkpoint.slproto.SLAgentCircuit

class SLDrawDistance : SLModule {
    val CHAT_RANGE: Float = 20.0f
    private val DRAW_RANGE_TIMEOUT: Long = 10000
    val MIN_DRAW_RANGE: Float = 10.5f
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
        var z: Boolean = true
        synchronized (this) {
            var f: Float = 10.5f
            if (this.worldViewActive) {
                f = max(10.5f, this.worldDrawDistance)
            }
            if (this.objectSelectionActive) {
                f = max(f, this.objectSelectDistance)
            }
            if (this.keepDrawDistance) {
                f = max(f, this.keepSelectDistance)
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
        this.worldDrawDistance = i.toFloat()
        if (!this.worldViewActive) {
            this.worldViewActive = true
            this.agentCircuit.getModules().avatarControl.EnableFastUpdates()
        }
        this.gridConn.parcelInfo.setDrawDistance(i.toFloat())
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
