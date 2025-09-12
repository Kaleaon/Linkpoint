// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.auth;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.auth:
//            SLAuthReply

public static class rightsHas
{

    public final int rightsGiven;
    public final int rightsHas;
    public final UUID uuid;

    public (UUID uuid1, int i, int j)
    {
        uuid = uuid1;
        rightsGiven = i;
        rightsHas = j;
    }
}
