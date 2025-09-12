// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.mutelist:
//            MuteType

public class MuteListEntry
{

    public static final int flagAll = 15;
    public static final int flagObjectSounds = 8;
    public static final int flagParticles = 4;
    public static final int flagTextChat = 1;
    public static final int flagVoiceChat = 2;
    public final int flags;
    public final String name;
    public final MuteType type;
    public final UUID uuid;

    public MuteListEntry(MuteType mutetype, UUID uuid1, String s, int i)
    {
        type = mutetype;
        uuid = UUIDPool.getUUID(uuid1);
        name = s;
        flags = i;
    }
}
