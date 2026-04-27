// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLLoginResultEvent
{

    public final UUID activeAgentUUID;
    public final String message;
    public final boolean success;

    public SLLoginResultEvent(boolean flag, String s, UUID uuid)
    {
        success = flag;
        message = s;
        activeAgentUUID = uuid;
    }
}
