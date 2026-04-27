// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.openjpeg;


// Referenced classes of package com.lumiyaviewer.lumiya.openjpeg:
//            OpenJPEG

public static final class  extends Enum
{

    private static final TGA $VALUES[];
    public static final TGA JPEG2000;
    public static final TGA Raw;
    public static final TGA TGA;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/openjpeg/OpenJPEG$ImageFormat, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Raw = new <init>("Raw", 0);
        JPEG2000 = new <init>("JPEG2000", 1);
        TGA = new <init>("TGA", 2);
        $VALUES = (new .VALUES[] {
            Raw, JPEG2000, TGA
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
