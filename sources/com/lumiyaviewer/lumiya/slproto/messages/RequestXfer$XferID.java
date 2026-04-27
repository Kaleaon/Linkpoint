// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            RequestXfer

public static class A
{

    public boolean DeleteOnCompletion;
    public int FilePath;
    public byte Filename[];
    public long ID;
    public boolean UseBigPackets;
    public UUID VFileID;
    public int VFileType;

    public A()
    {
    }
}
