// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


public final class AvatarTextureFaceIndex extends Enum
{

    private static final AvatarTextureFaceIndex $VALUES[];
    public static final AvatarTextureFaceIndex TEX_EYES_ALPHA;
    public static final AvatarTextureFaceIndex TEX_EYES_BAKED;
    public static final AvatarTextureFaceIndex TEX_EYES_IRIS;
    public static final AvatarTextureFaceIndex TEX_HAIR;
    public static final AvatarTextureFaceIndex TEX_HAIR_ALPHA;
    public static final AvatarTextureFaceIndex TEX_HAIR_BAKED;
    public static final AvatarTextureFaceIndex TEX_HEAD_ALPHA;
    public static final AvatarTextureFaceIndex TEX_HEAD_BAKED;
    public static final AvatarTextureFaceIndex TEX_HEAD_BODYPAINT;
    public static final AvatarTextureFaceIndex TEX_HEAD_TATTOO;
    public static final AvatarTextureFaceIndex TEX_LOWER_ALPHA;
    public static final AvatarTextureFaceIndex TEX_LOWER_BAKED;
    public static final AvatarTextureFaceIndex TEX_LOWER_BODYPAINT;
    public static final AvatarTextureFaceIndex TEX_LOWER_JACKET;
    public static final AvatarTextureFaceIndex TEX_LOWER_PANTS;
    public static final AvatarTextureFaceIndex TEX_LOWER_SHOES;
    public static final AvatarTextureFaceIndex TEX_LOWER_SOCKS;
    public static final AvatarTextureFaceIndex TEX_LOWER_TATTOO;
    public static final AvatarTextureFaceIndex TEX_LOWER_UNDERPANTS;
    public static final AvatarTextureFaceIndex TEX_SKIRT;
    public static final AvatarTextureFaceIndex TEX_SKIRT_BAKED;
    public static final AvatarTextureFaceIndex TEX_UPPER_ALPHA;
    public static final AvatarTextureFaceIndex TEX_UPPER_BAKED;
    public static final AvatarTextureFaceIndex TEX_UPPER_BODYPAINT;
    public static final AvatarTextureFaceIndex TEX_UPPER_GLOVES;
    public static final AvatarTextureFaceIndex TEX_UPPER_JACKET;
    public static final AvatarTextureFaceIndex TEX_UPPER_SHIRT;
    public static final AvatarTextureFaceIndex TEX_UPPER_TATTOO;
    public static final AvatarTextureFaceIndex TEX_UPPER_UNDERSHIRT;
    private String bakedTextureName;

    private AvatarTextureFaceIndex(String s, int i, String s1)
    {
        super(s, i);
        bakedTextureName = s1;
    }

    public static AvatarTextureFaceIndex valueOf(String s)
    {
        return (AvatarTextureFaceIndex)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex, s);
    }

    public static AvatarTextureFaceIndex[] values()
    {
        return $VALUES;
    }

    public String getBakedTextureName()
    {
        return bakedTextureName;
    }

    static 
    {
        TEX_HEAD_BODYPAINT = new AvatarTextureFaceIndex("TEX_HEAD_BODYPAINT", 0, "head");
        TEX_UPPER_SHIRT = new AvatarTextureFaceIndex("TEX_UPPER_SHIRT", 1, "upper");
        TEX_LOWER_PANTS = new AvatarTextureFaceIndex("TEX_LOWER_PANTS", 2, "lower");
        TEX_EYES_IRIS = new AvatarTextureFaceIndex("TEX_EYES_IRIS", 3, "eyes");
        TEX_HAIR = new AvatarTextureFaceIndex("TEX_HAIR", 4, "hair");
        TEX_UPPER_BODYPAINT = new AvatarTextureFaceIndex("TEX_UPPER_BODYPAINT", 5, "upper");
        TEX_LOWER_BODYPAINT = new AvatarTextureFaceIndex("TEX_LOWER_BODYPAINT", 6, "lower");
        TEX_LOWER_SHOES = new AvatarTextureFaceIndex("TEX_LOWER_SHOES", 7, "lower");
        TEX_HEAD_BAKED = new AvatarTextureFaceIndex("TEX_HEAD_BAKED", 8, "head");
        TEX_UPPER_BAKED = new AvatarTextureFaceIndex("TEX_UPPER_BAKED", 9, "upper");
        TEX_LOWER_BAKED = new AvatarTextureFaceIndex("TEX_LOWER_BAKED", 10, "lower");
        TEX_EYES_BAKED = new AvatarTextureFaceIndex("TEX_EYES_BAKED", 11, "eyes");
        TEX_LOWER_SOCKS = new AvatarTextureFaceIndex("TEX_LOWER_SOCKS", 12, "lower");
        TEX_UPPER_JACKET = new AvatarTextureFaceIndex("TEX_UPPER_JACKET", 13, "upper");
        TEX_LOWER_JACKET = new AvatarTextureFaceIndex("TEX_LOWER_JACKET", 14, "lower");
        TEX_UPPER_GLOVES = new AvatarTextureFaceIndex("TEX_UPPER_GLOVES", 15, "upper");
        TEX_UPPER_UNDERSHIRT = new AvatarTextureFaceIndex("TEX_UPPER_UNDERSHIRT", 16, "upper");
        TEX_LOWER_UNDERPANTS = new AvatarTextureFaceIndex("TEX_LOWER_UNDERPANTS", 17, "lower");
        TEX_SKIRT = new AvatarTextureFaceIndex("TEX_SKIRT", 18, "skirt");
        TEX_SKIRT_BAKED = new AvatarTextureFaceIndex("TEX_SKIRT_BAKED", 19, "skirt");
        TEX_HAIR_BAKED = new AvatarTextureFaceIndex("TEX_HAIR_BAKED", 20, "hair");
        TEX_LOWER_ALPHA = new AvatarTextureFaceIndex("TEX_LOWER_ALPHA", 21, "lower");
        TEX_UPPER_ALPHA = new AvatarTextureFaceIndex("TEX_UPPER_ALPHA", 22, "upper");
        TEX_HEAD_ALPHA = new AvatarTextureFaceIndex("TEX_HEAD_ALPHA", 23, "head");
        TEX_EYES_ALPHA = new AvatarTextureFaceIndex("TEX_EYES_ALPHA", 24, "eyes");
        TEX_HAIR_ALPHA = new AvatarTextureFaceIndex("TEX_HAIR_ALPHA", 25, "hair");
        TEX_HEAD_TATTOO = new AvatarTextureFaceIndex("TEX_HEAD_TATTOO", 26, "head");
        TEX_UPPER_TATTOO = new AvatarTextureFaceIndex("TEX_UPPER_TATTOO", 27, "upper");
        TEX_LOWER_TATTOO = new AvatarTextureFaceIndex("TEX_LOWER_TATTOO", 28, "lower");
        $VALUES = (new AvatarTextureFaceIndex[] {
            TEX_HEAD_BODYPAINT, TEX_UPPER_SHIRT, TEX_LOWER_PANTS, TEX_EYES_IRIS, TEX_HAIR, TEX_UPPER_BODYPAINT, TEX_LOWER_BODYPAINT, TEX_LOWER_SHOES, TEX_HEAD_BAKED, TEX_UPPER_BAKED, 
            TEX_LOWER_BAKED, TEX_EYES_BAKED, TEX_LOWER_SOCKS, TEX_UPPER_JACKET, TEX_LOWER_JACKET, TEX_UPPER_GLOVES, TEX_UPPER_UNDERSHIRT, TEX_LOWER_UNDERPANTS, TEX_SKIRT, TEX_SKIRT_BAKED, 
            TEX_HAIR_BAKED, TEX_LOWER_ALPHA, TEX_UPPER_ALPHA, TEX_HEAD_ALPHA, TEX_EYES_ALPHA, TEX_HAIR_ALPHA, TEX_HEAD_TATTOO, TEX_UPPER_TATTOO, TEX_LOWER_TATTOO
        });
    }
}
