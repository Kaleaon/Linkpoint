// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarRunningSequence, AvatarSkeleton

class AvatarRunningAnimation
    implements Comparable
{

    private final AnimationData.AnimationJointSet jointSet;
    private final AvatarRunningSequence sequence;

    AvatarRunningAnimation(AvatarRunningSequence avatarrunningsequence, AnimationData.AnimationJointSet animationjointset)
    {
        sequence = avatarrunningsequence;
        jointSet = animationjointset;
    }

    void animate(AvatarSkeleton avatarskeleton, float af[], float af1[], LLQuaternion allquaternion[], LLVector3 allvector3[])
    {
        jointSet.animate(avatarskeleton, sequence, af, af1, allquaternion, allvector3);
    }

    public int compareTo(AvatarRunningAnimation avatarrunninganimation)
    {
        int i = avatarrunninganimation.jointSet.getPriority() - jointSet.getPriority();
        if (i != 0)
        {
            return i;
        }
        i = avatarrunninganimation.sequence.sequenceID - sequence.sequenceID;
        if (i != 0)
        {
            return i;
        } else
        {
            return 0;
        }
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((AvatarRunningAnimation)obj);
    }
}
