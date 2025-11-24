package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.render.avatar.AnimationData.AnimationJointSet

class AvatarRunningAnimation(
    val sequence: AvatarRunningSequence,
    val jointSet: AnimationJointSet,
) : Comparable<AvatarRunningAnimation> {
    
    fun animate(
        avatarSkeleton: AvatarSkeleton,
        rotations: FloatArray,
        positions: FloatArray,
        quaternions: Array<LLQuaternion>,
        vectors: Array<LLVector3>,
    ) {
        // AnimationJointSet.animate signature:
        // animate(skeleton, timing, rotWeights, posWeights, rotations, positions)
        // sequence implements AnimationTiming (presumably)
        jointSet.animate(avatarSkeleton, sequence, rotations, positions, quaternions, vectors)
    }

    override fun compareTo(other: AvatarRunningAnimation): Int {
        val priorityDiff = other.jointSet.getPriority() - this.jointSet.getPriority()
        if (priorityDiff != 0) {
            return priorityDiff
        }

        // sequenceID might need to be Long or Int. Assuming Int or comparison logic matches.
        // If sequenceID is not available, we might need another way.
        // Assuming sequence has sequenceID
        val sequenceDiff = (other.sequence.sequenceID - this.sequence.sequenceID).toInt()
        return sequenceDiff
    }
}
