// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;

public class SLDrawDistance extends SLModule
{
    public static final float CHAT_RANGE = 20.0f;
    private static final long DRAW_RANGE_TIMEOUT = 10000L;
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
    
    public SLDrawDistance(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
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
    
    private void updateWantedDrawDistance() {
        final boolean b = true;
        monitorenter(this);
        float max = 10.5f;
        try {
            if (this.worldViewActive) {
                max = Math.max(10.5f, this.worldDrawDistance);
            }
            float max2 = max;
            if (this.objectSelectionActive) {
                max2 = Math.max(max, this.objectSelectDistance);
            }
            float max3 = max2;
            if (this.keepDrawDistance) {
                max3 = Math.max(max2, this.keepSelectDistance);
            }
            int keepDrawDistance = b ? 1 : 0;
            if (!this.worldViewActive) {
                keepDrawDistance = (b ? 1 : 0);
                if (!this.objectSelectionActive) {
                    keepDrawDistance = (this.keepDrawDistance ? 1 : 0);
                }
            }
            float wantedDrawDistance;
            if (keepDrawDistance == 0) {
                if (!this.defaultTimerSet) {
                    this.defaultDrawDistanceSince = System.currentTimeMillis();
                    this.defaultTimerSet = true;
                }
                wantedDrawDistance = max3;
                if (max3 != this.wantedDrawDistance) {
                    wantedDrawDistance = max3;
                    if (System.currentTimeMillis() < this.defaultDrawDistanceSince + 10000L) {
                        wantedDrawDistance = this.wantedDrawDistance;
                    }
                }
            }
            else {
                this.defaultTimerSet = false;
                wantedDrawDistance = max3;
            }
            this.wantedDrawDistance = wantedDrawDistance;
        }
        finally {
            monitorexit(this);
        }
    }
    
    public void Disable3DView() {
        synchronized (this) {
            Debug.Log("DrawDistance: Disable 3D View.");
            if (this.worldViewActive) {
                this.worldViewActive = false;
                this.agentCircuit.getModules().avatarControl.DisableFastUpdates();
            }
            this.agentCircuit.TryWakeUp();
        }
    }
    
    public void DisableKeepDistance() {
        synchronized (this) {
            this.keepDrawDistance = false;
        }
    }
    
    public void DisableObjectSelect() {
        synchronized (this) {
            this.objectSelectionActive = false;
            this.agentCircuit.TryWakeUp();
        }
    }
    
    public void Enable3DView(final int i) {
        synchronized (this) {
            Debug.Log("Enable3DView: Setting drawDistance to " + i);
            this.worldDrawDistance = (float)i;
            if (!this.worldViewActive) {
                this.worldViewActive = true;
                this.agentCircuit.getModules().avatarControl.EnableFastUpdates();
            }
            this.gridConn.parcelInfo.setDrawDistance((float)i);
            this.agentCircuit.TryWakeUp();
        }
    }
    
    public void EnableKeepDistance(final float keepSelectDistance) {
        synchronized (this) {
            this.keepDrawDistance = true;
            this.keepSelectDistance = keepSelectDistance;
        }
    }
    
    public void EnableObjectSelect() {
        synchronized (this) {
            this.objectSelectionActive = true;
            this.agentCircuit.TryWakeUp();
        }
    }
    
    public float getDrawDistanceForUpdate() {
        synchronized (this) {
            this.updateWantedDrawDistance();
            return this.activeDrawDistance = this.wantedDrawDistance;
        }
    }
    
    public float getObjectSelectRange() {
        synchronized (this) {
            return this.objectSelectDistance;
        }
    }
    
    public boolean is3DViewEnabled() {
        synchronized (this) {
            return this.worldViewActive;
        }
    }
    
    public boolean isObjectSelectEnabled() {
        synchronized (this) {
            return this.objectSelectionActive;
        }
    }
    
    public boolean needUpdateDrawDistance() {
        synchronized (this) {
            this.updateWantedDrawDistance();
            return this.wantedDrawDistance != this.activeDrawDistance;
        }
    }
    
    public void setObjectSelectRange(final float objectSelectDistance) {
        synchronized (this) {
            this.objectSelectDistance = objectSelectDistance;
            this.agentCircuit.TryWakeUp();
        }
    }
}
