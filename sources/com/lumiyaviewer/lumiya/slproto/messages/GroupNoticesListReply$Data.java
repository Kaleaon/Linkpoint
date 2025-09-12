// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            GroupNoticesListReply

public static class I
{

    public int AssetType;
    public byte FromName[];
    public boolean HasAttachment;
    public UUID NoticeID;
    public byte Subject[];
    public int Timestamp;

    public I()
    {
    }
}
