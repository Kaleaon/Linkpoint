package com.linkpoint.render.avatar

import com.google.common.collect.ImmutableList.Builder
import com.linkpoint.res.ResourceConsumer
import com.linkpoint.res.anim.AnimationCache
import java.lang.ref.WeakReference
import java.util.Collection
import javax.annotation.Nonnull
import javax.annotation.Nullable

class AvatarAnimationState : ResourceConsumer {
    private volatile AnimationData animationData
    private volatile AnimationPair animationPair = null
    private val WeakReference<DrawableAvatar> drawableAvatar
    private volatile AnimationSequenceInfo sequenceInfo

    @JvmStatic
private class AnimationPair {
        final AvatarRunningSequence runningAnimation
        final AvatarRunningSequence stoppingAnimation

        AnimationPair(AnimationSequenceInfo animationSequenceInfo, AnimationData animationData) {
            if (animationSequenceInfo.sequenceID != 0) {
                this.runningAnimation = AvatarRunningSequence(animationData, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, -1, animationSequenceInfo.dontEaseIn)
            } else {
                this.runningAnimation = null
            }
            if (animationSequenceInfo.stoppingSequenceID != 0) {
                this.stoppingAnimation = AvatarRunningSequence(animationData, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingEasingOutSince, animationSequenceInfo.dontEaseIn)
                return
            }
            this.stoppingAnimation = null
        }

        Unit getRunningAnimations(Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
            if (this.runningAnimation != null) {
                builder.add(this.runningAnimation)
                this.runningAnimation.getRunningAnimations(collection)
            }
            if (this.stoppingAnimation != null) {
                builder.add(this.stoppingAnimation)
                this.stoppingAnimation.getRunningAnimations(collection)
            }
        }

        Boolean hasStopped() {
            return this.runningAnimation != null ? false : this.stoppingAnimation != null ? this.stoppingAnimation.hasStopped() : true
        }
    }

    AvatarAnimationState(AnimationSequenceInfo animationSequenceInfo, DrawableAvatar drawableAvatar) {
        this.sequenceInfo = animationSequenceInfo
        this.drawableAvatar = WeakReference(drawableAvatar)
        AnimationCache.getInstance().RequestResource(animationSequenceInfo.animationID, this)
    }

    fun OnResourceReady(Object obj, Boolean z) {
        if (obj instanceof AnimationData) {
            this.animationData = (AnimationData) obj
            DrawableAvatar drawableAvatar = (DrawableAvatar) this.drawableAvatar.get()
            if (drawableAvatar != null) {
                drawableAvatar.updateRunningAnimations()
            }
        } else if (obj == null) {
            this.animationData = null
        }
    }

    Unit getRunningAnimations(Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
        AnimationPair animationPair = this.animationPair
        if (animationPair == null && this.animationData != null) {
            animationPair = AnimationPair(this.sequenceInfo, this.animationData)
            this.animationPair = animationPair
        }
        if (animationPair != null) {
            animationPair.getRunningAnimations(builder, collection)
        }
    }

    Boolean hasStopped() {
        AnimationPair animationPair = this.animationPair
        return animationPair != null ? animationPair.hasStopped() : false
    }

    Unit updateSequenceInfo(AnimationSequenceInfo animationSequenceInfo) {
        this.sequenceInfo = animationSequenceInfo
        this.animationPair = null
    }
}
