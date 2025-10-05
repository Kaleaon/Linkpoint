package com.linkpoint.render.avatar

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3

internal class AvatarRunningAnimation(
    private val sequence: AvatarRunningSequence,
    private val jointSet: AnimationJointSet
) : Comparable<AvatarRunningAnimation> {

    fun animate(
        avatarSkeleton: AvatarSkeleton,
        fArr: FloatArray,
        fArr2: FloatArray,
        lLQuaternionArr: Array<LLQuaternion>,
        lLVector3Arr: Array<LLVector3>
    ) {
        jointSet.animate(avatarSkeleton, sequence, fArr, fArr2, lLQuaternionArr, lLVector3Arr)
    }

    override fun compareTo(other: AvatarRunningAnimation): Int {
        val priority = other.jointSet.getPriority() - jointSet.getPriority()
        if (priority != 0) {
            return priority
        }
        val seqPriority = other.sequence.sequenceID - sequence.sequenceID
        return if (seqPriority != 0) seqPriority else 0
    }
}