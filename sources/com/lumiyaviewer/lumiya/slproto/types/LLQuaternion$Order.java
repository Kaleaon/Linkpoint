// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLQuaternion

public static final class  extends Enum
{

    private static final ZYX $VALUES[];
    public static final ZYX XYZ;
    public static final ZYX XZY;
    public static final ZYX YXZ;
    public static final ZYX YZX;
    public static final ZYX ZXY;
    public static final ZYX ZYX;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/types/LLQuaternion$Order, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        XYZ = new <init>("XYZ", 0);
        YZX = new <init>("YZX", 1);
        ZXY = new <init>("ZXY", 2);
        XZY = new <init>("XZY", 3);
        YXZ = new <init>("YXZ", 4);
        ZYX = new <init>("ZYX", 5);
        $VALUES = (new .VALUES[] {
            XYZ, YZX, ZXY, XZY, YXZ, ZYX
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
