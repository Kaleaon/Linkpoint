package com.linkpoint.render.avatar

class AnimationTiming {
    public Float inAnimationTime = 0.0f
    public Float inFactor = 0.0f
    public Float outFactor = 1.0f
    public Float runningTime = 0.0f

    public Boolean hasStopped() {
        return this.outFactor <= 0.0f
    }
}
