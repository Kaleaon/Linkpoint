// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.tex;


public final class TextureClass extends Enum
{

    private static final TextureClass $VALUES[];
    public static final TextureClass Asset;
    public static final TextureClass Baked;
    public static final TextureClass Prim;
    public static final TextureClass Sculpt;
    public static final TextureClass Terrain;
    private final String storePath;

    private TextureClass(String s, int i, String s1)
    {
        super(s, i);
        storePath = s1;
    }

    public static TextureClass valueOf(String s)
    {
        return (TextureClass)Enum.valueOf(com/lumiyaviewer/lumiya/render/tex/TextureClass, s);
    }

    public static TextureClass[] values()
    {
        return $VALUES;
    }

    public final String getStorePath()
    {
        return storePath;
    }

    static 
    {
        Prim = new TextureClass("Prim", 0, "textures");
        Sculpt = new TextureClass("Sculpt", 1, "sculpt");
        Baked = new TextureClass("Baked", 2, "baked");
        Asset = new TextureClass("Asset", 3, "asset");
        Terrain = new TextureClass("Terrain", 4, "terrain");
        $VALUES = (new TextureClass[] {
            Prim, Sculpt, Baked, Asset, Terrain
        });
    }
}
