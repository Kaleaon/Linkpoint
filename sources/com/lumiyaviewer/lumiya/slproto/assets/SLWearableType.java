// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;

public final class SLWearableType extends Enum
{

    private static final SLWearableType $VALUES[];
    public static final SLWearableType WT_ALPHA;
    public static final SLWearableType WT_EYES;
    public static final SLWearableType WT_GLOVES;
    public static final SLWearableType WT_HAIR;
    public static final SLWearableType WT_JACKET;
    public static final SLWearableType WT_PANTS;
    public static final SLWearableType WT_PHYSICS;
    public static final SLWearableType WT_SHAPE;
    public static final SLWearableType WT_SHIRT;
    public static final SLWearableType WT_SHOES;
    public static final SLWearableType WT_SKIN;
    public static final SLWearableType WT_SKIRT;
    public static final SLWearableType WT_SOCKS;
    public static final SLWearableType WT_TATTOO;
    public static final SLWearableType WT_UNDERPANTS;
    public static final SLWearableType WT_UNDERSHIRT;
    private SLAssetType assetType;
    private boolean isCritical;
    private String name;
    private int typeCode;

    private SLWearableType(String s, int i, int j, SLAssetType slassettype, boolean flag, String s1)
    {
        super(s, i);
        typeCode = j;
        assetType = slassettype;
        isCritical = flag;
        name = s1;
    }

    public static SLWearableType getByCode(int i)
    {
        SLWearableType aslwearabletype[] = values();
        int j = 0;
        for (int k = aslwearabletype.length; j < k; j++)
        {
            SLWearableType slwearabletype = aslwearabletype[j];
            if (slwearabletype.typeCode == i)
            {
                return slwearabletype;
            }
        }

        return null;
    }

    public static SLWearableType valueOf(String s)
    {
        return (SLWearableType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/assets/SLWearableType, s);
    }

    public static SLWearableType[] values()
    {
        return $VALUES;
    }

    public SLAssetType getAssetType()
    {
        return assetType;
    }

    public boolean getIsCritical()
    {
        return isCritical;
    }

    public String getName()
    {
        return name;
    }

    public int getTypeCode()
    {
        return typeCode;
    }

    public boolean isBodyPart()
    {
        return assetType == SLAssetType.AT_BODYPART;
    }

    static 
    {
        WT_SHAPE = new SLWearableType("WT_SHAPE", 0, 0, SLAssetType.AT_BODYPART, true, "Shape");
        WT_SKIN = new SLWearableType("WT_SKIN", 1, 1, SLAssetType.AT_BODYPART, true, "Skin");
        WT_HAIR = new SLWearableType("WT_HAIR", 2, 2, SLAssetType.AT_BODYPART, false, "Hair");
        WT_EYES = new SLWearableType("WT_EYES", 3, 3, SLAssetType.AT_BODYPART, false, "Eyes");
        WT_SHIRT = new SLWearableType("WT_SHIRT", 4, 4, SLAssetType.AT_CLOTHING, false, "Shirt");
        WT_PANTS = new SLWearableType("WT_PANTS", 5, 5, SLAssetType.AT_CLOTHING, false, "Pants");
        WT_SHOES = new SLWearableType("WT_SHOES", 6, 6, SLAssetType.AT_CLOTHING, false, "Shoes");
        WT_SOCKS = new SLWearableType("WT_SOCKS", 7, 7, SLAssetType.AT_CLOTHING, false, "Socks");
        WT_JACKET = new SLWearableType("WT_JACKET", 8, 8, SLAssetType.AT_CLOTHING, false, "Jacket");
        WT_GLOVES = new SLWearableType("WT_GLOVES", 9, 9, SLAssetType.AT_CLOTHING, false, "Gloves");
        WT_UNDERSHIRT = new SLWearableType("WT_UNDERSHIRT", 10, 10, SLAssetType.AT_CLOTHING, false, "Undershirt");
        WT_UNDERPANTS = new SLWearableType("WT_UNDERPANTS", 11, 11, SLAssetType.AT_CLOTHING, false, "Underpants");
        WT_SKIRT = new SLWearableType("WT_SKIRT", 12, 12, SLAssetType.AT_CLOTHING, false, "Skirt");
        WT_ALPHA = new SLWearableType("WT_ALPHA", 13, 13, SLAssetType.AT_CLOTHING, false, "Alpha");
        WT_TATTOO = new SLWearableType("WT_TATTOO", 14, 14, SLAssetType.AT_CLOTHING, false, "Tattoo");
        WT_PHYSICS = new SLWearableType("WT_PHYSICS", 15, 15, SLAssetType.AT_CLOTHING, false, "Physics");
        $VALUES = (new SLWearableType[] {
            WT_SHAPE, WT_SKIN, WT_HAIR, WT_EYES, WT_SHIRT, WT_PANTS, WT_SHOES, WT_SOCKS, WT_JACKET, WT_GLOVES, 
            WT_UNDERSHIRT, WT_UNDERPANTS, WT_SKIRT, WT_ALPHA, WT_TATTOO, WT_PHYSICS
        });
    }
}
