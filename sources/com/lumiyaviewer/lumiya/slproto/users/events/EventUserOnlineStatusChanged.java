// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventUserOnlineStatusChanged
{

    public final UUID agentUUID;
    public final boolean isOnline;
    public final UUID userUUID;

    public EventUserOnlineStatusChanged(UUID uuid, UUID uuid1, boolean flag)
    {
        agentUUID = uuid;
        userUUID = uuid1;
        isOnline = flag;
    }
}
