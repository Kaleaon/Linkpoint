// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLGridConnection

public static final class  extends Enum
{

    private static final Connected $VALUES[];
    public static final Connected Connected;
    public static final Connected Connecting;
    public static final Connected Idle;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/SLGridConnection$ConnectionState, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Idle = new <init>("Idle", 0);
        Connecting = new <init>("Connecting", 1);
        Connected = new <init>("Connected", 2);
        $VALUES = (new .VALUES[] {
            Idle, Connecting, Connected
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
