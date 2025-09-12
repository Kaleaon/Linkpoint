// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;


public class SLBakingProgressEvent
{

    public boolean done;
    public boolean first;
    public int progress;

    public SLBakingProgressEvent(boolean flag, boolean flag1, int i)
    {
        first = flag;
        done = flag1;
        progress = i;
    }
}
