package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import java.lang.ref.WeakReference;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class AvatarAnimationState implements ResourceConsumer {
    private volatile AnimationData animationData;

    @Nullable
    private volatile AnimationPair animationPair = null;

    @Nonnull
    private final WeakReference<DrawableAvatar> drawableAvatar;

    @Nonnull
    private volatile AnimationSequenceInfo sequenceInfo;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AnimationPair {

        @Nullable
        final AvatarRunningSequence runningAnimation;

        @Nullable
        final AvatarRunningSequence stoppingAnimation;

        AnimationPair(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull AnimationData animationData) {
            if (animationSequenceInfo.sequenceID != 0) {
                this.runningAnimation = new AvatarRunningSequence(animationData, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, -1L, animationSequenceInfo.dontEaseIn);
            } else {
                this.runningAnimation = null;
            }
            if (animationSequenceInfo.stoppingSequenceID != 0) {
                this.stoppingAnimation = new AvatarRunningSequence(animationData, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingEasingOutSince, animationSequenceInfo.dontEaseIn);
            } else {
                this.stoppingAnimation = null;
            }
        }

        void getRunningAnimations(ImmutableList.Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
            if (this.runningAnimation != null) {
                builder.add((ImmutableList.Builder<AvatarRunningSequence>) this.runningAnimation);
                this.runningAnimation.getRunningAnimations(collection);
            }
            if (this.stoppingAnimation != null) {
                builder.add((ImmutableList.Builder<AvatarRunningSequence>) this.stoppingAnimation);
                this.stoppingAnimation.getRunningAnimations(collection);
            }
        }

        boolean hasStopped() {
            if (this.runningAnimation != null) {
                return false;
            }
            if (this.stoppingAnimation != null) {
                return this.stoppingAnimation.hasStopped();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AvatarAnimationState(@Nonnull AnimationSequenceInfo animationSequenceInfo, @Nonnull DrawableAvatar drawableAvatar) {
        this.sequenceInfo = animationSequenceInfo;
        this.drawableAvatar = new WeakReference<>(drawableAvatar);
        AnimationCache.getInstance().RequestResource(animationSequenceInfo.animationID, this);
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
    public void OnResourceReady(Object obj, boolean z) {
        if (!(obj instanceof AnimationData)) {
            if (obj == null) {
                this.animationData = null;
            }
        } else {
            this.animationData = (AnimationData) obj;
            DrawableAvatar drawableAvatar = this.drawableAvatar.get();
            if (drawableAvatar != null) {
                drawableAvatar.updateRunningAnimations();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getRunningAnimations(ImmutableList.Builder<AvatarRunningSequence> builder, Collection<AvatarRunningAnimation> collection) {
        AnimationPair animationPair = this.animationPair;
        if (animationPair == null && this.animationData != null) {
            animationPair = new AnimationPair(this.sequenceInfo, this.animationData);
            this.animationPair = animationPair;
        }
        if (animationPair != null) {
            animationPair.getRunningAnimations(builder, collection);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasStopped() {
        AnimationPair animationPair = this.animationPair;
        if (animationPair != null) {
            return animationPair.hasStopped();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSequenceInfo(@Nonnull AnimationSequenceInfo animationSequenceInfo) {
        this.sequenceInfo = animationSequenceInfo;
        this.animationPair = null;
    }
}
