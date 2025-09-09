package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
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

        /* access modifiers changed from: package-private */
        public void getRunningAnimations(ImmutableList.Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
            if (this.runningAnimation != null) {
                builder.add((Object) this.runningAnimation);
                this.runningAnimation.getRunningAnimations(collection);
            }
            if (this.stoppingAnimation != null) {
                builder.add((Object) this.stoppingAnimation);
                this.stoppingAnimation.getRunningAnimations(collection);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean hasStopped() {
            if (this.runningAnimation != null) {
                return false;
            }
            if (this.stoppingAnimation != null) {
                return this.stoppingAnimation.hasStopped();
            }
            return true;
        }
    }

    AvatarAnimationState(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull DrawableAvatar drawableAvatar2) {
        this.sequenceInfo = animationSequenceInfo;
        this.drawableAvatar = new WeakReference<>(drawableAvatar2);
        AnimationCache.getInstance().RequestResource(animationSequenceInfo.animationID, this);
    }

    public void OnResourceReady(Object obj, boolean z) {
        if (obj instanceof AnimationData) {
            this.animationData = (AnimationData) obj;
            DrawableAvatar drawableAvatar2 = (DrawableAvatar) this.drawableAvatar.get();
            if (drawableAvatar2 != null) {
                drawableAvatar2.updateRunningAnimations();
            }
        } else if (obj == null) {
            this.animationData = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void getRunningAnimations(ImmutableList.Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
        AnimationPair animationPair2 = this.animationPair;
        if (animationPair2 == null && this.animationData != null) {
            animationPair2 = new AnimationPair(this.sequenceInfo, this.animationData);
            this.animationPair = animationPair2;
        }
        if (animationPair2 != null) {
            animationPair2.getRunningAnimations(builder, collection);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasStopped() {
        AnimationPair animationPair2 = this.animationPair;
        if (animationPair2 != null) {
            return animationPair2.hasStopped();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void updateSequenceInfo(@Nonnull AnimationSequenceInfo animationSequenceInfo) {
        this.sequenceInfo = animationSequenceInfo;
        this.animationPair = null;
    }
}
