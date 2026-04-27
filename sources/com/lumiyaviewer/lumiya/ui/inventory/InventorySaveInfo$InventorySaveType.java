// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventorySaveInfo

public static final class  extends Enum
{

    private static final InventoryOffer $VALUES[];
    public static final InventoryOffer InventoryOffer;
    public static final InventoryOffer NotecardItem;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/ui/inventory/InventorySaveInfo$InventorySaveType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        NotecardItem = new <init>("NotecardItem", 0);
        InventoryOffer = new <init>("InventoryOffer", 1);
        $VALUES = (new .VALUES[] {
            NotecardItem, InventoryOffer
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
