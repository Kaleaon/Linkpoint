// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat:
//            SLChatSessionMarkEvent

public static final class Q extends Enum
{

    private static final Teleport $VALUES[];
    public static final Teleport NewSession;
    public static final Teleport Teleport;

    public static Q valueOf(String s)
    {
        return (Q)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/SLChatSessionMarkEvent$SessionMarkType, s);
    }

    public static Q[] values()
    {
        return $VALUES;
    }

    static 
    {
        NewSession = new <init>("NewSession", 0);
        Teleport = new <init>("Teleport", 1);
        $VALUES = (new .VALUES[] {
            NewSession, Teleport
        });
    }

    private Q(String s, int i)
    {
        super(s, i);
    }
}
