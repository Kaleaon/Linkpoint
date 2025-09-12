// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
import java.util.Collection;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AnimationTiming, AnimationData

class AvatarRunningSequence extends AnimationTiming
{

    private final AnimationData animationData;
    private final boolean dontEaseIn;
    private final ImmutableList runningAnimations;
    private final long runningSince;
    final int sequenceID;
    private final long stoppingSince;

    AvatarRunningSequence(AnimationData animationdata, int i, long l, long l1, boolean flag)
    {
        animationData = animationdata;
        sequenceID = i;
        runningSince = l;
        stoppingSince = l1;
        dontEaseIn = flag;
        runningAnimations = animationdata.createRunningAnimations(this);
    }

    public int getAnimationPriority()
    {
        return animationData.getPriority();
    }

    void getRunningAnimations(Collection collection)
    {
        collection.addAll(runningAnimations);
    }

    boolean needAnimate(long l)
    {
        return animationData.updateAnimationTiming(l, runningSince, stoppingSince, dontEaseIn, this);
    }
}
