// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import java.util.UUID;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class AvatarAnimationState implements ResourceConsumer
{
    private volatile AnimationData animationData;
    @Nullable
    private volatile AnimationPair animationPair;
    @Nonnull
    private final WeakReference<DrawableAvatar> drawableAvatar;
    @Nonnull
    private volatile AnimationSequenceInfo sequenceInfo;
    
    AvatarAnimationState(@Nonnull final AnimationSequenceInfo sequenceInfo, @Nonnull final DrawableAvatar referent) {
        this.animationPair = null;
        this.sequenceInfo = sequenceInfo;
        this.drawableAvatar = new WeakReference<DrawableAvatar>(referent);
        ((ResourceMemoryCache<UUID, ResourceType>)AnimationCache.getInstance()).RequestResource(sequenceInfo.animationID, this);
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        if (o instanceof AnimationData) {
            this.animationData = (AnimationData)o;
            final DrawableAvatar drawableAvatar = this.drawableAvatar.get();
            if (drawableAvatar != null) {
                drawableAvatar.updateRunningAnimations();
            }
        }
        else if (o == null) {
            this.animationData = null;
        }
    }
    
    void getRunningAnimations(final ImmutableList.Builder<AvatarRunningSequence> builder, final Collection<AvatarRunningAnimation> collection) {
        AnimationPair animationPair2;
        final AnimationPair animationPair = animationPair2 = this.animationPair;
        if (animationPair == null) {
            animationPair2 = animationPair;
            if (this.animationData != null) {
                animationPair2 = new AnimationPair(this.sequenceInfo, this.animationData);
                this.animationPair = animationPair2;
            }
        }
        if (animationPair2 != null) {
            animationPair2.getRunningAnimations(builder, collection);
        }
    }
    
    boolean hasStopped() {
        final AnimationPair animationPair = this.animationPair;
        return animationPair != null && animationPair.hasStopped();
    }
    
    void updateSequenceInfo(@Nonnull final AnimationSequenceInfo sequenceInfo) {
        this.sequenceInfo = sequenceInfo;
        this.animationPair = null;
    }
    
    private static class AnimationPair
    {
        @Nullable
        final AvatarRunningSequence runningAnimation;
        @Nullable
        final AvatarRunningSequence stoppingAnimation;
        
        AnimationPair(@Nonnull final AnimationSequenceInfo animationSequenceInfo, @Nonnull final AnimationData animationData) {
            if (animationSequenceInfo.sequenceID != 0) {
                this.runningAnimation = new AvatarRunningSequence(animationData, animationSequenceInfo.sequenceID, animationSequenceInfo.runningSince, -1L, animationSequenceInfo.dontEaseIn);
            }
            else {
                this.runningAnimation = null;
            }
            if (animationSequenceInfo.stoppingSequenceID != 0) {
                this.stoppingAnimation = new AvatarRunningSequence(animationData, animationSequenceInfo.stoppingSequenceID, animationSequenceInfo.stoppingRunningSince, animationSequenceInfo.stoppingEasingOutSince, animationSequenceInfo.dontEaseIn);
            }
            else {
                this.stoppingAnimation = null;
            }
        }
        
        void getRunningAnimations(final ImmutableList.Builder<AvatarRunningSequence> builder, final Collection<AvatarRunningAnimation> collection) {
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
            return this.runningAnimation == null && (this.stoppingAnimation == null || this.stoppingAnimation.hasStopped());
        }
    }
}
