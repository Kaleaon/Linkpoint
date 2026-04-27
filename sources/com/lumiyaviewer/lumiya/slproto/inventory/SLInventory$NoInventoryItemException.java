// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory

public static class  extends Exception
{

    private static final long serialVersionUID = 1L;

    public (long l)
    {
        super((new StringBuilder()).append("Inventory item ").append(l).append(" not found").toString());
    }

    public (UUID uuid)
    {
        super((new StringBuilder()).append("Inventory item ").append(uuid.toString()).append(" not found").toString());
    }
}
