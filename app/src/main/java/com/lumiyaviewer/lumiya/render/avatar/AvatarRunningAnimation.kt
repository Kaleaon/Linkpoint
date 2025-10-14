package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3

internal class AvatarRunningAnimation(
    private val sequence: AvatarRunningSequence,
    private val jointSet: AnimationJointSet,
) : Comparable<AvatarRunningAnimation> {
    fun animate(
        avatarSkeleton: AvatarSkeleton,
        rotations: FloatArray,
        positions: FloatArray,
        quaternions: Array<LLQuaternion>,
        vectors: Array<LLVector3>,
    ) {
        jointSet.animate(avatarSkeleton, sequence, rotations, positions, quaternions, vectors)
    }

    override fun compareTo(other: AvatarRunningAnimation): Int {
        val priorityDiff = other.jointSet.priority - this.jointSet.priority
        if (priorityDiff != 0) {
            return priorityDiff
        }

        val sequenceDiff = other.sequence.sequenceID - this.sequence.sequenceID
        return if (sequenceDiff != 0) sequenceDiff else 0
    }
}
