// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;


public final class SLAvatarGlobalColor extends Enum
{

    private static final SLAvatarGlobalColor $VALUES[];
    public static final SLAvatarGlobalColor eye_color;
    public static final SLAvatarGlobalColor hair_color;
    public static final SLAvatarGlobalColor skin_color;
    private int paramIDs[];

    private SLAvatarGlobalColor(String s, int i, int ai[])
    {
        super(s, i);
        paramIDs = ai;
    }

    public static SLAvatarGlobalColor valueOf(String s)
    {
        return (SLAvatarGlobalColor)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/baker/SLAvatarGlobalColor, s);
    }

    public static SLAvatarGlobalColor[] values()
    {
        return $VALUES;
    }

    public int[] getParamIDs()
    {
        return paramIDs;
    }

    static 
    {
        skin_color = new SLAvatarGlobalColor("skin_color", 0, new int[] {
            111, 110, 108
        });
        hair_color = new SLAvatarGlobalColor("hair_color", 1, new int[] {
            114, 113, 115, 112
        });
        eye_color = new SLAvatarGlobalColor("eye_color", 2, new int[] {
            99, 98
        });
        $VALUES = (new SLAvatarGlobalColor[] {
            skin_color, hair_color, eye_color
        });
    }
}
