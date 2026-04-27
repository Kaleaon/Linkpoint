// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryActivity

public static final class subtitleResourceId extends Enum
{

    private static final applyPickImage $VALUES[];
    public static final applyPickImage applyFirstLife;
    public static final applyPickImage applyPickImage;
    public static final applyPickImage applyUserProfile;
    public final int subtitleResourceId;

    public static subtitleResourceId valueOf(String s)
    {
        return (subtitleResourceId)Enum.valueOf(com/lumiyaviewer/lumiya/ui/inventory/InventoryActivity$SelectAction, s);
    }

    public static subtitleResourceId[] values()
    {
        return $VALUES;
    }

    static 
    {
        applyUserProfile = new <init>("applyUserProfile", 0, 0x7f0902f8);
        applyFirstLife = new <init>("applyFirstLife", 1, 0x7f0902f8);
        applyPickImage = new <init>("applyPickImage", 2, 0x7f0902f8);
        $VALUES = (new .VALUES[] {
            applyUserProfile, applyFirstLife, applyPickImage
        });
    }

    private I(String s, int i, int j)
    {
        super(s, i);
        subtitleResourceId = j;
    }
}
