// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventUserInfoChanged
{

    public static final int CHANGED_NAME = 2;
    public static final int CHANGED_PROFILE = 4;
    public final UUID agentUUID;
    public final int changedMask;
    public final UUID userUUID;

    public EventUserInfoChanged(UUID uuid, UUID uuid1, int i)
    {
        agentUUID = uuid;
        userUUID = uuid1;
        changedMask = i;
    }

    public boolean isNameChanged()
    {
        boolean flag = false;
        if ((changedMask & 2) != 0)
        {
            flag = true;
        }
        return flag;
    }

    public boolean isProfileChanged()
    {
        boolean flag = false;
        if ((changedMask & 4) != 0)
        {
            flag = true;
        }
        return flag;
    }
}
