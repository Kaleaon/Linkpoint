package com.linkpoint.render.avatar

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableList.Builder
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.Iterator
import javax.annotation.Nonnull

class AvatarAnimationList {
    private val ImmutableList<AvatarRunningAnimation> animations
    private val ImmutableList<AvatarRunningSequence> sequences

    AvatarAnimationList(Collection<AvatarAnimationState> collection) {
        val arrayList: Collection = ArrayList(collection.size())
        val builder: Builder = ImmutableList.builder()
        for (AvatarAnimationState runningAnimations : collection) {
            runningAnimations.getRunningAnimations(builder, arrayList)
        }
        Collections.sort(arrayList)
        this.sequences = builder.build()
        this.animations = ImmutableList.copyOf(arrayList)
    }

     fun animate(avatarSkeleton: AvatarSkeleton, fArr: FloatArray, fArr2: FloatArray, lLQuaternionArr: Array<LLQuaternion>, lLVector3Arr: Array<LLVector3>) {
        for (AvatarRunningAnimation animate : this.animations) {
            animate.animate(avatarSkeleton, fArr, fArr2, lLQuaternionArr, lLVector3Arr)
        }
        val length: Int = fArr.length
        for (Int i = 0; i < length; i++) {
            val f: Float = 1.0f - fArr[i]
            if (f > 0.01f && f < 1.0f) {
                f = 1.0f / f
                val lLQuaternion: LLQuaternion = lLQuaternionArr[i]
                lLQuaternion.x *= f
                lLQuaternion = lLQuaternionArr[i]
                lLQuaternion.y *= f
                lLQuaternion = lLQuaternionArr[i]
                lLQuaternion.z *= f
                lLQuaternion = lLQuaternionArr[i]
                lLQuaternion.w = f * lLQuaternion.w
            }
        }
    }

     fun needAnimate(j: Long): Boolean {
        val z: Boolean = false
        val it: Iterator = this.sequences.iterator()
        while (true) {
            val z2: Boolean = z
            if (!it.hasNext()) {
                return z2
            }
            z = ((AvatarRunningSequence) it.next()).needAnimate(j) | z2
        }
    }
}
