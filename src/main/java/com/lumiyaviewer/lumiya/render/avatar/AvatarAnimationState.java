package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList.Builder;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import java.lang.ref.WeakReference;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AvatarAnimationState implements ResourceConsumer {
    private volatile AnimationData animationData;
    @Nullable
    private volatile AnimationPair animationPair = null;
    @Nonnull
    private final WeakReference<DrawableAvatar> drawableAvatar;
    @Nonnull
    private volatile AnimationSequenceInfo sequenceInfo;

    private static class AnimationPair {
        @Nullable
        final AvatarRunningSequence runningAnimation;
        @Nullable
        final AvatarRunningSequence stoppingAnimation;

        AnimationPair(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull AnimationData animationData) {
            if (animationSequenceInfo.sequenceID != 0) {
                this.runningAnimation = new AvatarRunningSequence(animationData, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, -1, animationSequenceInfo.dontEaseIn);
            } else {
                this.runningAnimation = null;
            }
            if (animationSequenceInfo.stoppingSequenceID != 0) {
                this.stoppingAnimation = new AvatarRunningSequence(animationData, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingEasingOutSince, animationSequenceInfo.dontEaseIn);
                return;
            }
            this.stoppingAnimation = null;
        }

        void getRunningAnimations(Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
            if (this.runningAnimation != null) {
                builder.add(this.runningAnimation);
                this.runningAnimation.getRunningAnimations(collection);
            }
            if (this.stoppingAnimation != null) {
                builder.add(this.stoppingAnimation);
                this.stoppingAnimation.getRunningAnimations(collection);
            }
        }

        boolean hasStopped() {
            return this.runningAnimation != null ? false : this.stoppingAnimation != null ? this.stoppingAnimation.hasStopped() : true;
        }
    }

    AvatarAnimationState(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull DrawableAvatar drawableAvatar) {
        this.sequenceInfo = animationSequenceInfo;
        this.drawableAvatar = new WeakReference(drawableAvatar);
        AnimationCache.getInstance().RequestResource(animationSequenceInfo.animationID, this);
    }

    public void OnResourceReady(Object obj, boolean z) {
        if (obj instanceof AnimationData) {
            this.animationData = (AnimationData) obj;
            DrawableAvatar drawableAvatar = (DrawableAvatar) this.drawableAvatar.get();
            if (drawableAvatar != null) {
                drawableAvatar.updateRunningAnimations();
            }
        } else if (obj == null) {
            this.animationData = null;
        }
    }

    void getRunningAnimations(Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
        AnimationPair animationPair = this.animationPair;
        if (animationPair == null && this.animationData != null) {
            animationPair = new AnimationPair(this.sequenceInfo, this.animationData);
            this.animationPair = animationPair;
        }
        if (animationPair != null) {
            animationPair.getRunningAnimations(builder, collection);
        }
    }

    boolean hasStopped() {
        AnimationPair animationPair = this.animationPair;
        return animationPair != null ? animationPair.hasStopped() : false;
    }

    void updateSequenceInfo(@Nonnull AnimationSequenceInfo animationSequenceInfo) {
        this.sequenceInfo = animationSequenceInfo;
        this.animationPair = null;
    }
}
