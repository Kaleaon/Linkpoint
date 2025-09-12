// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;


public class SLDisconnectEvent
{

    public final String message;
    public final boolean normalDisconnect;

    public SLDisconnectEvent(boolean flag, String s)
    {
        normalDisconnect = flag;
        message = s;
    }
}
