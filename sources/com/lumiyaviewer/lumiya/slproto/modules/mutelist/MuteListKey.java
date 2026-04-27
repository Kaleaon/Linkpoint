// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.mutelist:
//            MuteListEntry, MuteType

public class MuteListKey
{

    public final MuteType muteType;
    public final UUID uuid;

    public MuteListKey(MuteListEntry mutelistentry)
    {
        muteType = mutelistentry.type;
        uuid = mutelistentry.uuid;
    }

    public MuteListKey(MuteType mutetype, UUID uuid1)
    {
        muteType = mutetype;
        uuid = uuid1;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof MuteListKey))
        {
            return false;
        }
        obj = (MuteListKey)obj;
        if (muteType != ((MuteListKey) (obj)).muteType)
        {
            return false;
        }
        boolean flag;
        boolean flag1;
        if (uuid == null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (((MuteListKey) (obj)).uuid == null)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        if (flag != flag1)
        {
            return false;
        }
        return uuid == null || uuid.equals(((MuteListKey) (obj)).uuid);
    }

    public int hashCode()
    {
        int i = 0;
        if (muteType != null)
        {
            i = muteType.hashCode() + 0;
        }
        int j = i;
        if (uuid != null)
        {
            j = i + uuid.hashCode();
        }
        return j;
    }
}
