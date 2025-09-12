// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLAvatarParamColor

public static final class  extends Enum
{

    private static final Multiply $VALUES[];
    public static final Multiply Blend;
    public static final Multiply Default;
    public static final Multiply Multiply;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/avatar/SLAvatarParamColor$ColorOperation, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Default = new <init>("Default", 0);
        Blend = new <init>("Blend", 1);
        Multiply = new <init>("Multiply", 2);
        $VALUES = (new .VALUES[] {
            Default, Blend, Multiply
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
