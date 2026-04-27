// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;


// Referenced classes of package com.lumiyaviewer.lumiya.voice.common.model:
//            VoiceChatInfo

public static final class  extends Enum
{

    private static final Active $VALUES[];
    public static final Active Active;
    public static final Active Connecting;
    public static final Active None;
    public static final Active Ringing;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/voice/common/model/VoiceChatInfo$VoiceChatState, s);
    }

    public static [] values()
    {
        return ([])$VALUES.clone();
    }

    static 
    {
        None = new <init>("None", 0);
        Ringing = new <init>("Ringing", 1);
        Connecting = new <init>("Connecting", 2);
        Active = new <init>("Active", 3);
        $VALUES = (new .VALUES[] {
            None, Ringing, Connecting, Active
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
