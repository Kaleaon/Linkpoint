package com.lumiyaviewer.lumiya.render.avatar;

/* loaded from: classes.dex */
public class AnimationTiming {
    public float runningTime = 0.0f;
    public float inAnimationTime = 0.0f;
    public float inFactor = 0.0f;
    public float outFactor = 1.0f;

    public boolean hasStopped() {
        return this.outFactor <= 0.0f;
    }
}
