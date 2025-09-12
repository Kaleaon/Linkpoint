// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.tex;


public final class TexturePriority extends Enum
{

    private static final TexturePriority $VALUES[];
    public static final TexturePriority Asset;
    public static final TexturePriority Lowest;
    public static final TexturePriority PrimInvisible;
    public static final TexturePriority PrimVisibleClose;
    public static final TexturePriority PrimVisibleFar;
    public static final TexturePriority PrimVisibleMedium;
    public static final TexturePriority Sculpt;
    public static final TexturePriority Terrain;

    private TexturePriority(String s, int i)
    {
        super(s, i);
    }

    public static TexturePriority valueOf(String s)
    {
        return (TexturePriority)Enum.valueOf(com/lumiyaviewer/lumiya/render/tex/TexturePriority, s);
    }

    public static TexturePriority[] values()
    {
        return $VALUES;
    }

    static 
    {
        Asset = new TexturePriority("Asset", 0);
        Sculpt = new TexturePriority("Sculpt", 1);
        PrimVisibleClose = new TexturePriority("PrimVisibleClose", 2);
        PrimVisibleMedium = new TexturePriority("PrimVisibleMedium", 3);
        Terrain = new TexturePriority("Terrain", 4);
        PrimVisibleFar = new TexturePriority("PrimVisibleFar", 5);
        PrimInvisible = new TexturePriority("PrimInvisible", 6);
        Lowest = new TexturePriority("Lowest", 7);
        $VALUES = (new TexturePriority[] {
            Asset, Sculpt, PrimVisibleClose, PrimVisibleMedium, Terrain, PrimVisibleFar, PrimInvisible, Lowest
        });
    }
}
