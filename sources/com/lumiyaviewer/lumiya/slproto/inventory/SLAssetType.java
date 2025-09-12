// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.google.common.collect.ImmutableMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryType

public final class SLAssetType extends Enum
{

    private static final SLAssetType $VALUES[];
    public static final SLAssetType AT_ANIMATION;
    public static final SLAssetType AT_BODYPART;
    public static final SLAssetType AT_CALLINGCARD;
    public static final SLAssetType AT_CATEGORY;
    public static final SLAssetType AT_CLOTHING;
    public static final SLAssetType AT_GESTURE;
    public static final SLAssetType AT_IMAGE_JPEG;
    public static final SLAssetType AT_IMAGE_TGA;
    public static final SLAssetType AT_LANDMARK;
    public static final SLAssetType AT_LINK;
    public static final SLAssetType AT_LINK_FOLDER;
    public static final SLAssetType AT_LSL_BYTECODE;
    public static final SLAssetType AT_LSL_TEXT;
    public static final SLAssetType AT_MESH;
    public static final SLAssetType AT_NOTECARD;
    public static final SLAssetType AT_OBJECT;
    public static final SLAssetType AT_SCRIPT;
    public static final SLAssetType AT_SIMSTATE;
    public static final SLAssetType AT_SOUND;
    public static final SLAssetType AT_SOUND_WAV;
    public static final SLAssetType AT_TEXTURE;
    public static final SLAssetType AT_TEXTURE_TGA;
    public static final SLAssetType AT_UNKNOWN;
    public static final SLAssetType AT_WIDGET;
    private static final ImmutableMap tagMap;
    private final int actionDescription;
    private final int drawableResource;
    private final SLInventoryType invType;
    private final int specialFolderType;
    private final String stringCode;
    private final int typeCode;
    private final int typeDescription;

    private SLAssetType(String s, int i, int j, String s1, SLInventoryType slinventorytype, int k, int l, 
            int i1, int j1)
    {
        super(s, i);
        typeCode = j;
        stringCode = s1;
        invType = slinventorytype;
        specialFolderType = k;
        drawableResource = l;
        typeDescription = i1;
        actionDescription = j1;
    }

    public static SLAssetType getByString(String s)
    {
        SLAssetType slassettype = (SLAssetType)tagMap.get(s);
        s = slassettype;
        if (slassettype == null)
        {
            s = AT_UNKNOWN;
        }
        return s;
    }

    public static SLAssetType getByType(int i)
    {
        SLAssetType aslassettype[] = values();
        int j = 0;
        for (int k = aslassettype.length; j < k; j++)
        {
            SLAssetType slassettype = aslassettype[j];
            if (slassettype.typeCode == i)
            {
                return slassettype;
            }
        }

        return AT_UNKNOWN;
    }

    public static SLAssetType valueOf(String s)
    {
        return (SLAssetType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/inventory/SLAssetType, s);
    }

    public static SLAssetType[] values()
    {
        return $VALUES;
    }

    public int getActionDescription()
    {
        return actionDescription;
    }

    public int getDrawableResource()
    {
        return drawableResource;
    }

    public SLInventoryType getInventoryType()
    {
        return invType;
    }

    public int getSpecialFolderType()
    {
        return specialFolderType;
    }

    public String getStringCode()
    {
        return stringCode;
    }

    public int getTypeCode()
    {
        return typeCode;
    }

    public int getTypeDescription()
    {
        return typeDescription;
    }

    static 
    {
        AT_TEXTURE = new SLAssetType("AT_TEXTURE", 0, 0, "texture", SLInventoryType.IT_TEXTURE, 0, 0x7f0200c2, 0x7f090075, 0x7f090050);
        AT_SOUND = new SLAssetType("AT_SOUND", 1, 1, "sound", SLInventoryType.IT_SOUND, 1, 0x7f0200ca, 0x7f090074, -1);
        AT_CALLINGCARD = new SLAssetType("AT_CALLINGCARD", 2, 2, "callcard", SLInventoryType.IT_CALLINGCARD, 2, 0x7f0200cd, 0x7f090065, -1);
        AT_LANDMARK = new SLAssetType("AT_LANDMARK", 3, 3, "landmark", SLInventoryType.IT_LANDMARK, 3, 0x7f0200c3, 0x7f090069, 0x7f09004f);
        AT_SCRIPT = new SLAssetType("AT_SCRIPT", 4, 4, "script", SLInventoryType.IT_LSL, 10, 0x7f0200c8, 0x7f090071, 0x7f09004d);
        AT_CLOTHING = new SLAssetType("AT_CLOTHING", 5, 5, "clothing", SLInventoryType.IT_WEARABLE, 5, 0x7f0200bf, 0x7f090066, -1);
        AT_OBJECT = new SLAssetType("AT_OBJECT", 6, 6, "object", SLInventoryType.IT_OBJECT, 6, 0x7f0200c6, 0x7f09006f, 0x7f09004e);
        AT_NOTECARD = new SLAssetType("AT_NOTECARD", 7, 7, "notecard", SLInventoryType.IT_NOTECARD, 7, 0x7f0200c5, 0x7f09006e, 0x7f09004d);
        AT_CATEGORY = new SLAssetType("AT_CATEGORY", 8, 8, "category", SLInventoryType.IT_CATEGORY, -1, 0x7f0200c0, 0x7f090067, -1);
        AT_LSL_TEXT = new SLAssetType("AT_LSL_TEXT", 9, 10, "lsltext", SLInventoryType.IT_LSL, 10, 0x7f0200c8, 0x7f09006c, 0x7f09004d);
        AT_LSL_BYTECODE = new SLAssetType("AT_LSL_BYTECODE", 10, 11, "lslbyte", SLInventoryType.IT_LSL, 10, 0x7f0200c8, 0x7f09006d, -1);
        AT_TEXTURE_TGA = new SLAssetType("AT_TEXTURE_TGA", 11, 12, "txtr_tga", SLInventoryType.IT_TEXTURE, 0, 0x7f0200c2, 0x7f090076, -1);
        AT_BODYPART = new SLAssetType("AT_BODYPART", 12, 13, "bodypart", SLInventoryType.IT_WEARABLE, 13, 0x7f0200c1, 0x7f090064, -1);
        AT_SOUND_WAV = new SLAssetType("AT_SOUND_WAV", 13, 17, "snd_wav", SLInventoryType.IT_SOUND, 1, 0x7f0200ca, 0x7f090074, -1);
        AT_IMAGE_TGA = new SLAssetType("AT_IMAGE_TGA", 14, 18, "img_tga", SLInventoryType.IT_TEXTURE, 0, 0x7f0200c2, 0x7f090076, -1);
        AT_IMAGE_JPEG = new SLAssetType("AT_IMAGE_JPEG", 15, 19, "jpeg", SLInventoryType.IT_TEXTURE, 0, 0x7f0200c2, 0x7f090075, -1);
        AT_ANIMATION = new SLAssetType("AT_ANIMATION", 16, 20, "animatn", SLInventoryType.IT_ANIMATION, 20, 0x7f0200bd, 0x7f090062, -1);
        AT_GESTURE = new SLAssetType("AT_GESTURE", 17, 21, "gesture", SLInventoryType.IT_GESTURE, 21, 0x7f0200c9, 0x7f090068, -1);
        AT_SIMSTATE = new SLAssetType("AT_SIMSTATE", 18, 22, "simstate", SLInventoryType.IT_UNKNOWN, 6, 0x7f0200c8, 0x7f090072, -1);
        AT_LINK = new SLAssetType("AT_LINK", 19, 24, "link", SLInventoryType.IT_UNKNOWN, 6, 0x7f0200c4, 0x7f09006a, -1);
        AT_LINK_FOLDER = new SLAssetType("AT_LINK_FOLDER", 20, 25, "link_f", SLInventoryType.IT_UNKNOWN, 6, 0x7f0200c4, 0x7f09006a, -1);
        AT_MESH = new SLAssetType("AT_MESH", 21, 49, "mesh", SLInventoryType.IT_MESH, 6, 0x7f0200c6, 0x7f09006f, -1);
        AT_WIDGET = new SLAssetType("AT_WIDGET", 22, 40, "widget", SLInventoryType.IT_WIDGET, 6, 0x7f0200c6, 0x7f09006f, -1);
        AT_UNKNOWN = new SLAssetType("AT_UNKNOWN", 23, -1, "unknown", SLInventoryType.IT_UNKNOWN, -1, -1, -1, -1);
        $VALUES = (new SLAssetType[] {
            AT_TEXTURE, AT_SOUND, AT_CALLINGCARD, AT_LANDMARK, AT_SCRIPT, AT_CLOTHING, AT_OBJECT, AT_NOTECARD, AT_CATEGORY, AT_LSL_TEXT, 
            AT_LSL_BYTECODE, AT_TEXTURE_TGA, AT_BODYPART, AT_SOUND_WAV, AT_IMAGE_TGA, AT_IMAGE_JPEG, AT_ANIMATION, AT_GESTURE, AT_SIMSTATE, AT_LINK, 
            AT_LINK_FOLDER, AT_MESH, AT_WIDGET, AT_UNKNOWN
        });
        com.google.common.collect.ImmutableMap.Builder builder = ImmutableMap.builder();
        SLAssetType aslassettype[] = values();
        int i = 0;
        for (int j = aslassettype.length; i < j; i++)
        {
            SLAssetType slassettype = aslassettype[i];
            builder.put(slassettype.stringCode, slassettype);
        }

        tagMap = builder.build();
    }
}
