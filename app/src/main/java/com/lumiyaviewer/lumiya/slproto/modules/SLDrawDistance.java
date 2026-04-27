package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;

public class SLDrawDistance extends SLModule {
    public static final float CHAT_RANGE = 20.0f;
    private static final long DRAW_RANGE_TIMEOUT = 10000;
    public static final float MIN_DRAW_RANGE = 10.5f;
    private float activeDrawDistance = 0.0f;
    private long defaultDrawDistanceSince = 0;
    private boolean defaultTimerSet = false;
    private volatile boolean keepDrawDistance = false;
    private float keepSelectDistance = 20.0f;
    private float objectSelectDistance = 20.0f;
    private volatile boolean objectSelectionActive = false;
    private float wantedDrawDistance = 10.5f;
    private float worldDrawDistance = 20.0f;
    private volatile boolean worldViewActive = false;

    public SLDrawDistance(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
    }

    private synchronized void updateWantedDrawDistance() {
        boolean z = true;
        synchronized (this) {
            float f = 10.5f;
            if (this.worldViewActive) {
                f = Math.max(10.5f, this.worldDrawDistance);
            }
            if (this.objectSelectionActive) {
                f = Math.max(f, this.objectSelectDistance);
            }
            if (this.keepDrawDistance) {
                f = Math.max(f, this.keepSelectDistance);
            }
            if (!this.worldViewActive && !this.objectSelectionActive) {
                z = this.keepDrawDistance;
            }
            if (!z) {
                if (!this.defaultTimerSet) {
                    this.defaultDrawDistanceSince = System.currentTimeMillis();
                    this.defaultTimerSet = true;
                }
                if (f != this.wantedDrawDistance && System.currentTimeMillis() < this.defaultDrawDistanceSince + DRAW_RANGE_TIMEOUT) {
                    f = this.wantedDrawDistance;
                }
            } else {
                this.defaultTimerSet = false;
            }
            this.wantedDrawDistance = f;
        }
    }

    public synchronized void Disable3DView() {
        Debug.Log("DrawDistance: Disable 3D View.");
        if (this.worldViewActive) {
            this.worldViewActive = false;
            this.agentCircuit.getModules().avatarControl.DisableFastUpdates();
        }
        this.agentCircuit.TryWakeUp();
    }

    public synchronized void DisableKeepDistance() {
        this.keepDrawDistance = false;
    }

    public synchronized void DisableObjectSelect() {
        this.objectSelectionActive = false;
        this.agentCircuit.TryWakeUp();
    }

    public synchronized void Enable3DView(int i) {
        Debug.Log("Enable3DView: Setting drawDistance to " + i);
        this.worldDrawDistance = (float) i;
        if (!this.worldViewActive) {
            this.worldViewActive = true;
            this.agentCircuit.getModules().avatarControl.EnableFastUpdates();
        }
        this.gridConn.parcelInfo.setDrawDistance((float) i);
        this.agentCircuit.TryWakeUp();
    }

    public synchronized void EnableKeepDistance(float f) {
        this.keepDrawDistance = true;
        this.keepSelectDistance = f;
    }

    public synchronized void EnableObjectSelect() {
        this.objectSelectionActive = true;
        this.agentCircuit.TryWakeUp();
    }

    public synchronized float getDrawDistanceForUpdate() {
        updateWantedDrawDistance();
        this.activeDrawDistance = this.wantedDrawDistance;
        return this.activeDrawDistance;
    }

    public synchronized float getObjectSelectRange() {
        return this.objectSelectDistance;
    }

    public synchronized boolean is3DViewEnabled() {
        return this.worldViewActive;
    }

    public synchronized boolean isObjectSelectEnabled() {
        return this.objectSelectionActive;
    }

    public synchronized boolean needUpdateDrawDistance() {
        updateWantedDrawDistance();
        return this.wantedDrawDistance != this.activeDrawDistance;
    }

    public synchronized void setObjectSelectRange(float f) {
        this.objectSelectDistance = f;
        this.agentCircuit.TryWakeUp();
    }
}
