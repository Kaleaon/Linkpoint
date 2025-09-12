// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import com.google.common.base.Strings;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            InternPool

public class UUIDPool extends InternPool
{

    public static final UUID ZeroUUID = new UUID(0L, 0L);
    private static final UUIDPool instance = new UUIDPool();

    public UUIDPool()
    {
    }

    public static final UUIDPool getInstance()
    {
        return instance;
    }

    public static UUID getUUID(long l, long l1)
    {
        return (UUID)instance.intern(new UUID(l, l1));
    }

    public static UUID getUUID(String s)
    {
        if (!Strings.isNullOrEmpty(s))
        {
            return (UUID)instance.intern(UUID.fromString(s));
        } else
        {
            return null;
        }
    }

    public static UUID getUUID(UUID uuid)
    {
        return (UUID)instance.intern(uuid);
    }

    public static UUID setUUID(UUID uuid, long l, long l1)
    {
        if (uuid != null && uuid.getMostSignificantBits() == l && uuid.getLeastSignificantBits() == l1)
        {
            return uuid;
        } else
        {
            return (UUID)instance.intern(new UUID(l, l1));
        }
    }

}
