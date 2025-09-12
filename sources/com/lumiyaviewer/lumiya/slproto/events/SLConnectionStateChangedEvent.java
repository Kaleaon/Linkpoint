// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;


public class SLConnectionStateChangedEvent
{

    public final com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState connectionState;

    public SLConnectionStateChangedEvent(com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState connectionstate)
    {
        connectionState = connectionstate;
    }
}
