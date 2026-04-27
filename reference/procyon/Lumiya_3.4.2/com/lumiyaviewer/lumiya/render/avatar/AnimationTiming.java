// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

public class AnimationTiming
{
    public float inAnimationTime;
    public float inFactor;
    public float outFactor;
    public float runningTime;
    
    public AnimationTiming() {
        this.runningTime = 0.0f;
        this.inAnimationTime = 0.0f;
        this.inFactor = 0.0f;
        this.outFactor = 1.0f;
    }
    
    public boolean hasStopped() {
        return this.outFactor <= 0.0f;
    }
}
