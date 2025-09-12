// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.anim.AnimationCache;
import java.lang.ref.WeakReference;
import java.util.Collection;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AnimationSequenceInfo, AnimationData, DrawableAvatar, AvatarRunningSequence

public class AvatarAnimationState
    implements ResourceConsumer
{
    private static class AnimationPair
    {

        final AvatarRunningSequence runningAnimation;
        final AvatarRunningSequence stoppingAnimation;

        void getRunningAnimations(com.google.common.collect.ImmutableList.Builder builder, Collection collection)
        {
            if (runningAnimation != null)
            {
                builder.add(runningAnimation);
                runningAnimation.getRunningAnimations(collection);
            }
            if (stoppingAnimation != null)
            {
                builder.add(stoppingAnimation);
                stoppingAnimation.getRunningAnimations(collection);
            }
        }

        boolean hasStopped()
        {
            if (runningAnimation != null)
            {
                return false;
            }
            if (stoppingAnimation != null)
            {
                return stoppingAnimation.hasStopped();
            } else
            {
                return true;
            }
        }

        AnimationPair(AnimationSequenceInfo animationsequenceinfo, AnimationData animationdata)
        {
            if (animationsequenceinfo.sequenceID != 0)
            {
                runningAnimation = new AvatarRunningSequence(animationdata, animationsequenceinfo.sequenceID, animationsequenceinfo.runningSince, -1L, animationsequenceinfo.dontEaseIn);
            } else
            {
                runningAnimation = null;
            }
            if (animationsequenceinfo.stoppingSequenceID != 0)
            {
                stoppingAnimation = new AvatarRunningSequence(animationdata, animationsequenceinfo.stoppingSequenceID, animationsequenceinfo.stoppingRunningSince, animationsequenceinfo.stoppingEasingOutSince, animationsequenceinfo.dontEaseIn);
                return;
            } else
            {
                stoppingAnimation = null;
                return;
            }
        }
    }


    private volatile AnimationData animationData;
    private volatile AnimationPair animationPair;
    private final WeakReference drawableAvatar;
    private volatile AnimationSequenceInfo sequenceInfo;

    AvatarAnimationState(AnimationSequenceInfo animationsequenceinfo, DrawableAvatar drawableavatar)
    {
        animationPair = null;
        sequenceInfo = animationsequenceinfo;
        drawableAvatar = new WeakReference(drawableavatar);
        AnimationCache.getInstance().RequestResource(animationsequenceinfo.animationID, this);
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof AnimationData)
        {
            animationData = (AnimationData)obj;
            obj = (DrawableAvatar)drawableAvatar.get();
            if (obj != null)
            {
                ((DrawableAvatar) (obj)).updateRunningAnimations();
            }
        } else
        if (obj == null)
        {
            animationData = null;
            return;
        }
    }

    void getRunningAnimations(com.google.common.collect.ImmutableList.Builder builder, Collection collection)
    {
        AnimationPair animationpair1 = animationPair;
        AnimationPair animationpair = animationpair1;
        if (animationpair1 == null)
        {
            animationpair = animationpair1;
            if (animationData != null)
            {
                animationpair = new AnimationPair(sequenceInfo, animationData);
                animationPair = animationpair;
            }
        }
        if (animationpair != null)
        {
            animationpair.getRunningAnimations(builder, collection);
        }
    }

    boolean hasStopped()
    {
        AnimationPair animationpair = animationPair;
        if (animationpair != null)
        {
            return animationpair.hasStopped();
        } else
        {
            return false;
        }
    }

    void updateSequenceInfo(AnimationSequenceInfo animationsequenceinfo)
    {
        sequenceInfo = animationsequenceinfo;
        animationPair = null;
    }
}
