// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;


public final class MoveControl extends Enum
{

    private static final MoveControl $VALUES[];
    public static final MoveControl Backward;
    public static final MoveControl Forward;
    public static final MoveControl Left;
    public static final MoveControl Right;

    private MoveControl(String s, int i)
    {
        super(s, i);
    }

    public static MoveControl valueOf(String s)
    {
        return (MoveControl)Enum.valueOf(com/lumiyaviewer/lumiya/ui/render/MoveControl, s);
    }

    public static MoveControl[] values()
    {
        return $VALUES;
    }

    static 
    {
        Forward = new MoveControl("Forward", 0);
        Backward = new MoveControl("Backward", 1);
        Left = new MoveControl("Left", 2);
        Right = new MoveControl("Right", 3);
        $VALUES = (new MoveControl[] {
            Forward, Backward, Left, Right
        });
    }
}
