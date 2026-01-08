package com.lumiyaviewer.lumiya.render.avatar

import com.google.common.collect.ImmutableList

class AvatarRunningSequence(
    private val animationData: AnimationData,
    val sequenceID: Int,
    private val runningSince: Long,
    private val stoppingSince: Long,
    private val dontEaseIn: Boolean
) : AnimationTiming() {
    
    private val runningAnimations: ImmutableList<AvatarRunningAnimation> =
        animationData.createRunningAnimations(this)

    fun getAnimationPriority(): Int {
        return animationData.getPriority()
    }

    fun getRunningAnimations(collection: MutableCollection<AvatarRunningAnimation>) {
        collection.addAll(runningAnimations)
    }

    fun needAnimate(currentTime: Long): Boolean {
        return animationData.updateAnimationTiming(
            currentTime,
            runningSince,
            stoppingSince,
            dontEaseIn,
            this
        )
    }
}
