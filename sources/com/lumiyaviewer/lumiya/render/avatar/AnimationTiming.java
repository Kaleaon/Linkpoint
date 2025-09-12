// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;


public class AnimationTiming
{

    public float inAnimationTime;
    public float inFactor;
    public float outFactor;
    public float runningTime;

    public AnimationTiming()
    {
        runningTime = 0.0F;
        inAnimationTime = 0.0F;
        inFactor = 0.0F;
        outFactor = 1.0F;
    }

    public boolean hasStopped()
    {
        return outFactor <= 0.0F;
    }
}
