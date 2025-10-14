package com.lumiyaviewer.lumiya.render.avatar

import com.google.common.collect.ImmutableList.Builder
import com.lumiyaviewer.lumiya.res.ResourceConsumer
import com.lumiyaviewer.lumiya.res.anim.AnimationCache
import java.lang.ref.WeakReference
import java.util.Collection
import javax.annotation.Nonnull
import javax.annotation.Nullable

class AvatarAnimationState : ResourceConsumer {
    private volatile AnimationData animationData
    @Nullable
    private volatile AnimationPair animationPair = null
    @Nonnull
    private WeakReference<DrawableAvatar> drawableAvatar
    @Nonnull
    private volatile AnimationSequenceInfo sequenceInfo

    private class AnimationPair {
        @Nullable
        AvatarRunningSequence runningAnimation
        @Nullable
        AvatarRunningSequence stoppingAnimation

        AnimationPair(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull AnimationData animationData) {
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

    AvatarAnimationState(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull DrawableAvatar drawableAvatar) {
        this.sequenceInfo = animationSequenceInfo
        this.drawableAvatar = WeakReference(drawableAvatar)
        AnimationCache.getInstance().RequestResource(animationSequenceInfo.animationID, this)
    }

    fun OnResourceReady(obj: Any, z: Boolean): Unit {
        if (obj instanceof AnimationData) {
            this.animationData = (AnimationData) obj
            DrawableAvatar drawableAvatar = (DrawableAvatar) this.drawableAvatar.get()
            drawableAvatar?.updateRunningAnimations()
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
        animationPair?.getRunningAnimations(builder, collection)
        }
    }

    Boolean hasStopped() {
        AnimationPair animationPair = this.animationPair
        return animationPair != null ? animationPair.hasStopped() : false
    }

    Unit updateSequenceInfo(@Nonnull AnimationSequenceInfo animationSequenceInfo) {
        this.sequenceInfo = animationSequenceInfo
        this.animationPair = null
    }
}
