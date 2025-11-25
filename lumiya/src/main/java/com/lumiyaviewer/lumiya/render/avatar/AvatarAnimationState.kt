package com.lumiyaviewer.lumiya.render.avatar

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.anim.AnimationCache
import java.lang.ref.WeakReference
import java.util.Collection

class AvatarAnimationState(
    @Volatile private var sequenceInfo: AnimationSequenceInfo,
    drawableAvatar: DrawableAvatar
) : ResourceConsumer {
    
    @Volatile 
    private var animationData: AnimationData? = null
    
    @Volatile
    private var animationPair: AnimationPair? = null
    
    private val drawableAvatarRef = WeakReference(drawableAvatar)

    init {
        AnimationCache.getInstance().RequestResource(sequenceInfo.animationID, this)
    }

    private class AnimationPair(
        animationSequenceInfo: AnimationSequenceInfo,
        animationData: AnimationData
    ) {
        var runningAnimation: AvatarRunningSequence? = null
        var stoppingAnimation: AvatarRunningSequence? = null

        init {
            if (animationSequenceInfo.sequenceID != 0) {
                runningAnimation = AvatarRunningSequence(
                    animationData,
                    animationSequenceInfo.sequenceID,
                    animationSequenceInfo.runningSince,
                    -1,
                    animationSequenceInfo.dontEaseIn
                )
            } else {
                runningAnimation = null
            }
            
            if (animationSequenceInfo.stoppingSequenceID != 0) {
                stoppingAnimation = AvatarRunningSequence(
                    animationData,
                    animationSequenceInfo.stoppingSequenceID,
                    animationSequenceInfo.stoppingRunningSince,
                    animationSequenceInfo.stoppingEasingOutSince,
                    animationSequenceInfo.dontEaseIn
                )
            } else {
                stoppingAnimation = null
            }
        }

        fun getRunningAnimations(
            builder: ImmutableList.Builder<AvatarRunningSequence>,
            collection: MutableCollection<AvatarRunningAnimation>
        ) {
            runningAnimation?.let {
                builder.add(it)
                it.getRunningAnimations(collection)
            }
            stoppingAnimation?.let {
                builder.add(it)
                it.getRunningAnimations(collection)
            }
        }

        fun hasStopped(): Boolean {
            if (runningAnimation != null) return false
            return stoppingAnimation?.hasStopped() ?: true // Assuming hasStopped is in AvatarRunningSequence or AnimationTiming
        }
    }

    override fun OnResourceReady(obj: Any?, z: Boolean) {
        if (obj is AnimationData) {
            this.animationData = obj
            val drawableAvatar = drawableAvatarRef.get()
            // drawableAvatar?.updateRunningAnimations()
        } else if (obj == null) {
            this.animationData = null
        }
    }

    fun getRunningAnimations(
        builder: ImmutableList.Builder<AvatarRunningSequence>,
        collection: MutableCollection<AvatarRunningAnimation>
    ) {
        var pair = this.animationPair
        val data = this.animationData
        
        if (pair == null && data != null) {
            pair = AnimationPair(sequenceInfo, data)
            this.animationPair = pair
        }
        
        pair?.getRunningAnimations(builder, collection)
    }

    fun hasStopped(): Boolean {
        return animationPair?.hasStopped() ?: false
    }

    fun updateSequenceInfo(animationSequenceInfo: AnimationSequenceInfo) {
        this.sequenceInfo = animationSequenceInfo
        this.animationPair = null
    }
}
