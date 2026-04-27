// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.https;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.https:
//            LLSDContentTypeDetector

public static final class  extends Enum
{

    private static final llsdBinary $VALUES[];
    public static final llsdBinary llsdBinary;
    public static final llsdBinary llsdXML;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/https/LLSDContentTypeDetector$LLSDContentType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        llsdXML = new <init>("llsdXML", 0);
        llsdBinary = new <init>("llsdBinary", 1);
        $VALUES = (new .VALUES[] {
            llsdXML, llsdBinary
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
