// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;


public final class MuteType extends Enum
{

    private static final MuteType $VALUES[];
    public static final MuteType AGENT;
    public static final MuteType BY_NAME;
    public static final MuteType EXTERNAL;
    public static final MuteType GROUP;
    public static final MuteType OBJECT;
    private int viewOrder;

    private MuteType(String s, int i, int j)
    {
        super(s, i);
        viewOrder = j;
    }

    public static MuteType valueOf(String s)
    {
        return (MuteType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/mutelist/MuteType, s);
    }

    public static MuteType[] values()
    {
        return $VALUES;
    }

    public int getViewOrder()
    {
        return viewOrder;
    }

    static 
    {
        BY_NAME = new MuteType("BY_NAME", 0, 2);
        AGENT = new MuteType("AGENT", 1, 0);
        OBJECT = new MuteType("OBJECT", 2, 1);
        GROUP = new MuteType("GROUP", 3, 3);
        EXTERNAL = new MuteType("EXTERNAL", 4, 4);
        $VALUES = (new MuteType[] {
            BY_NAME, AGENT, OBJECT, GROUP, EXTERNAL
        });
    }
}
