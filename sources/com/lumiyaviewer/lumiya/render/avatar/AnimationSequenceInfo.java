// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import java.util.UUID;

public class AnimationSequenceInfo
{

    protected static final int INVALID_SEQUENCE_ID = 0;
    protected static final long INVALID_TIMESTAMP = -1L;
    public static final long MAX_ANIMATION_LENGTH = 60000L;
    protected final UUID animationID;
    protected final boolean dontEaseIn;
    protected final long runningSince;
    protected final int sequenceID;
    protected final long stoppingEasingOutSince;
    protected final long stoppingRunningSince;
    protected final int stoppingSequenceID;

    private AnimationSequenceInfo(UUID uuid, int i, long l, int j, long l1, 
            long l2, boolean flag)
    {
        animationID = uuid;
        sequenceID = i;
        runningSince = l;
        stoppingSequenceID = j;
        stoppingRunningSince = l1;
        stoppingEasingOutSince = l2;
        dontEaseIn = flag;
    }

    public static AnimationSequenceInfo newSequence(UUID uuid, long l, int i)
    {
        return new AnimationSequenceInfo(uuid, i, l, 0, -1L, -1L, false);
    }

    public static AnimationSequenceInfo restartSequence(long l, int i, AnimationSequenceInfo animationsequenceinfo)
    {
        if (animationsequenceinfo.sequenceID != 0)
        {
            return new AnimationSequenceInfo(animationsequenceinfo.animationID, i, l, animationsequenceinfo.sequenceID, animationsequenceinfo.runningSince, l, true);
        } else
        {
            return new AnimationSequenceInfo(animationsequenceinfo.animationID, i, l, animationsequenceinfo.stoppingSequenceID, animationsequenceinfo.stoppingRunningSince, animationsequenceinfo.stoppingRunningSince, true);
        }
    }

    public static AnimationSequenceInfo stopSequence(long l, AnimationSequenceInfo animationsequenceinfo)
    {
        if (animationsequenceinfo.sequenceID != 0)
        {
            return new AnimationSequenceInfo(animationsequenceinfo.animationID, 0, -1L, animationsequenceinfo.sequenceID, animationsequenceinfo.runningSince, l, animationsequenceinfo.dontEaseIn);
        } else
        {
            return null;
        }
    }

    public boolean hasStopped(long l)
    {
        return sequenceID == 0 && (stoppingSequenceID == 0 || l >= stoppingEasingOutSince + 60000L);
    }
}
