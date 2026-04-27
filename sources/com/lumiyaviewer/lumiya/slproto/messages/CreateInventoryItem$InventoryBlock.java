// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            CreateInventoryItem

public static class Q
{

    public int CallbackID;
    public byte Description[];
    public UUID FolderID;
    public int InvType;
    public byte Name[];
    public int NextOwnerMask;
    public UUID TransactionID;
    public int Type;
    public int WearableType;

    public Q()
    {
    }
}
