package com.linkpoint.render.avatar

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import javax.annotation.Nonnull

class AvatarRunningAnimation : Comparable<AvatarRunningAnimation> {
    private val AnimationJointSet jointSet
    private val AvatarRunningSequence sequence

    AvatarRunningAnimation(AvatarRunningSequence avatarRunningSequence, AnimationJointSet animationJointSet) {
        this.sequence = avatarRunningSequence
        this.jointSet = animationJointSet
    }

    Unit animate(AvatarSkeleton avatarSkeleton, Float[] fArr, Float[] fArr2, LLQuaternion[] lLQuaternionArr, LLVector3[] lLVector3Arr) {
        this.jointSet.animate(avatarSkeleton, this.sequence, fArr, fArr2, lLQuaternionArr, lLVector3Arr)
    }

    public Int compareTo(AvatarRunningAnimation avatarRunningAnimation) {
        Int priority = avatarRunningAnimation.jointSet.getPriority() - this.jointSet.getPriority()
        if (priority != 0) {
            return priority
        }
        priority = avatarRunningAnimation.sequence.sequenceID - this.sequence.sequenceID
        return priority != 0 ? priority : 0
    }
}
