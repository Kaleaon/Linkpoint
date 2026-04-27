// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            InventoryManager

public static class inventoryEntry
{

    public final SLInventoryEntry inventoryEntry;
    public final boolean isCut;

    public (boolean flag, SLInventoryEntry slinventoryentry)
    {
        isCut = flag;
        inventoryEntry = slinventoryentry;
    }
}
