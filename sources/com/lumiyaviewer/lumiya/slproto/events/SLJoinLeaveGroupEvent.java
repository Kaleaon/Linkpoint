// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLJoinLeaveGroupEvent
{

    public final UUID groupID;
    public final boolean isJoin;
    public final boolean success;

    public SLJoinLeaveGroupEvent(UUID uuid, boolean flag, boolean flag1)
    {
        groupID = uuid;
        isJoin = flag;
        success = flag1;
    }
}
