// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            AvatarPickerForInvite

private static class <init>
{

    final UUID roleID;
    final String roleTitle;

    public String toString()
    {
        return roleTitle;
    }

    private (UUID uuid, String s)
    {
        roleID = uuid;
        roleTitle = s;
    }

    roleTitle(UUID uuid, String s, roleTitle roletitle)
    {
        this(uuid, s);
    }
}
