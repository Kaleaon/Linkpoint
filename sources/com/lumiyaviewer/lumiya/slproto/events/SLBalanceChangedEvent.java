// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;


public class SLBalanceChangedEvent
{

    public final int newBalance;
    public final int oldBalance;
    public final boolean oldBalanceValid;

    public SLBalanceChangedEvent(boolean flag, int i, int j)
    {
        oldBalanceValid = flag;
        oldBalance = i;
        newBalance = j;
    }
}
