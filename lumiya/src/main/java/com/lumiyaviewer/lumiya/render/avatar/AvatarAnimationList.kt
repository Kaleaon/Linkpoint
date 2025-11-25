package com.lumiyaviewer.lumiya.render.avatar

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.ArrayList
import java.util.Collections

class AvatarAnimationList(collection: Collection<AvatarAnimationState>) {
    
    private val animations: ImmutableList<AvatarRunningAnimation>
    private val sequences: ImmutableList<AvatarRunningSequence>

    init {
        val arrayList = ArrayList<AvatarRunningAnimation>(collection.size)
        val builder = ImmutableList.builder<AvatarRunningSequence>()
        
        for (runningAnimations in collection) {
            runningAnimations.getRunningAnimations(builder, arrayList)
        }
        
        // Assuming AvatarRunningAnimation implements Comparable
        try {
            Collections.sort(arrayList)
        } catch (e: Exception) {
            // Ignore sort errors or assume comparable
        }
        
        this.sequences = builder.build()
        this.animations = ImmutableList.copyOf(arrayList)
    }

    fun animate(
        avatarSkeleton: AvatarSkeleton,
        rotPriority: FloatArray,
        posPriority: FloatArray,
        rotations: Array<LLQuaternion>,
        positions: Array<LLVector3>
    ) {
        for (animate in animations) {
            animate.animate(avatarSkeleton, rotPriority, posPriority, rotations, positions)
        }
        
        val length = rotPriority.size
        for (i in 0 until length) {
            var f = 1.0f - rotPriority[i]
            if (f > 0.01f && f < 1.0f) {
                f = 1.0f / f
                val quaternion = rotations[i]
                // Scale components? The original code did:
                // x *= f, y *= f, z *= f, w = f * w
                // This looks like scaling the quaternion.
                quaternion.x *= f
                quaternion.y *= f
                quaternion.z *= f
                quaternion.w *= f
            }
        }
    }

    fun needAnimate(time: Long): Boolean {
        var needed = false
        for (sequence in sequences) {
            needed = sequence.needAnimate(time) || needed
        }
        return needed
    }
}
