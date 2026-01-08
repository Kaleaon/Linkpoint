package com.lumiyaviewer.lumiya.render.avatar

open class AnimationTiming {
    var inAnimationTime = 0.0f
    var inFactor = 0.0f
    var outFactor = 1.0f
    var runningTime = 0.0f

    fun hasStopped(): Boolean = outFactor <= 0.0f
}
