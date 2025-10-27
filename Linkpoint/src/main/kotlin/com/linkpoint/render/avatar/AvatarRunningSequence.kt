package com.linkpoint.render.avatar

import com.google.common.collect.ImmutableList
import java.util.Collection
import javax.annotation.Nonnull

class AvatarRunningSequence : AnimationTiming() {
    private val AnimationData animationData
    private val Boolean dontEaseIn
    private val ImmutableList<AvatarRunningAnimation> runningAnimations
    private val Long runningSince
    final Int sequenceID
    private val Long stoppingSince

    AvatarRunningSequence(AnimationData animationData, Int i, Long j, Long j2, Boolean z) {
        this.animationData = animationData
        this.sequenceID = i
        this.runningSince = j
        this.stoppingSince = j2
        this.dontEaseIn = z
        this.runningAnimations = animationData.createRunningAnimations(this)
    }

     public fun getAnimationPriority(): Int {
        return this.animationData.getPriority()
    }

     fun getRunningAnimations(collection: Collection<AvatarRunningAnimation>) {
        collection.addAll(this.runningAnimations)
    }

     fun needAnimate(j: Long): Boolean {
        return this.animationData.updateAnimationTiming(j, this.runningSince, this.stoppingSince, this.dontEaseIn, this)
    }
}
