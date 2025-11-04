// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import java.util.Collection;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;

class AvatarRunningSequence extends AnimationTiming
{
    @Nonnull
    private final AnimationData animationData;
    private final boolean dontEaseIn;
    private final ImmutableList<AvatarRunningAnimation> runningAnimations;
    private final long runningSince;
    final int sequenceID;
    private final long stoppingSince;
    
    AvatarRunningSequence(@Nonnull final AnimationData animationData, final int sequenceID, final long runningSince, final long stoppingSince, final boolean dontEaseIn) {
        this.animationData = animationData;
        this.sequenceID = sequenceID;
        this.runningSince = runningSince;
        this.stoppingSince = stoppingSince;
        this.dontEaseIn = dontEaseIn;
        this.runningAnimations = animationData.createRunningAnimations(this);
    }
    
    public int getAnimationPriority() {
        return this.animationData.getPriority();
    }
    
    void getRunningAnimations(final Collection<AvatarRunningAnimation> collection) {
        collection.addAll(this.runningAnimations);
    }
    
    boolean needAnimate(final long n) {
        return this.animationData.updateAnimationTiming(n, this.runningSince, this.stoppingSince, this.dontEaseIn, this);
    }
}
