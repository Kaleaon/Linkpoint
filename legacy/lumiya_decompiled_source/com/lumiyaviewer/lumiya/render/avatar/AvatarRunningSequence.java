package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nonnull;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AvatarRunningSequence extends AnimationTiming {
    @Nonnull
    private final AnimationData animationData;
    private final boolean dontEaseIn;
    private final ImmutableList<AvatarRunningAnimation> runningAnimations;
    private final long runningSince;
    final int sequenceID;
    private final long stoppingSince;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AvatarRunningSequence(@Nonnull AnimationData animationData, int i, long j, long j2, boolean z) {
        this.animationData = animationData;
        this.sequenceID = i;
        this.runningSince = j;
        this.stoppingSince = j2;
        this.dontEaseIn = z;
        this.runningAnimations = animationData.createRunningAnimations(this);
    }

    public int getAnimationPriority() {
        return this.animationData.getPriority();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getRunningAnimations(Collection<AvatarRunningAnimation> collection) {
        collection.addAll(this.runningAnimations);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean needAnimate(long j) {
        return this.animationData.updateAnimationTiming(j, this.runningSince, this.stoppingSince, this.dontEaseIn, this);
    }
}
