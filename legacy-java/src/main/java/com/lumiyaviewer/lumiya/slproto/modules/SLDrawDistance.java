package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;

/* loaded from: classes.dex */
public class SLDrawDistance extends SLModule {
    public static final float CHAT_RANGE = 20.0f;
    private static final long DRAW_RANGE_TIMEOUT = 10000;
    public static final float MIN_DRAW_RANGE = 10.5f;
    private float activeDrawDistance;
    private long defaultDrawDistanceSince;
    private boolean defaultTimerSet;
    private volatile boolean keepDrawDistance;
    private float keepSelectDistance;
    private float objectSelectDistance;
    private volatile boolean objectSelectionActive;
    private float wantedDrawDistance;
    private float worldDrawDistance;
    private volatile boolean worldViewActive;

    public SLDrawDistance(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
        this.worldViewActive = false;
        this.objectSelectionActive = false;
        this.keepDrawDistance = false;
        this.worldDrawDistance = 20.0f;
        this.objectSelectDistance = 20.0f;
        this.keepSelectDistance = 20.0f;
        this.activeDrawDistance = 0.0f;
        this.wantedDrawDistance = 10.5f;
        this.defaultDrawDistanceSince = 0L;
        this.defaultTimerSet = false;
    }

    private synchronized void updateWantedDrawDistance() {
        boolean z = true;
        synchronized (this) {
            float max = this.worldViewActive ? Math.max(10.5f, this.worldDrawDistance) : 10.5f;
            if (this.objectSelectionActive) {
                max = Math.max(max, this.objectSelectDistance);
            }
            if (this.keepDrawDistance) {
                max = Math.max(max, this.keepSelectDistance);
            }
            if (!this.worldViewActive && !this.objectSelectionActive) {
                z = this.keepDrawDistance;
            }
            if (z) {
                this.defaultTimerSet = false;
            } else {
                if (!this.defaultTimerSet) {
                    this.defaultDrawDistanceSince = System.currentTimeMillis();
                    this.defaultTimerSet = true;
                }
                if (max != this.wantedDrawDistance && System.currentTimeMillis() < this.defaultDrawDistanceSince + DRAW_RANGE_TIMEOUT) {
                    max = this.wantedDrawDistance;
                }
            }
            this.wantedDrawDistance = max;
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
        this.worldDrawDistance = i;
        if (!this.worldViewActive) {
            this.worldViewActive = true;
            this.agentCircuit.getModules().avatarControl.EnableFastUpdates();
        }
        this.gridConn.parcelInfo.setDrawDistance(i);
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
