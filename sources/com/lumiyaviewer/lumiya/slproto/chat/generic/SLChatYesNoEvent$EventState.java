// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            SLChatYesNoEvent

public static final class  extends Enum
{

    private static final EventCancelled $VALUES[];
    public static final EventCancelled EventAccepted;
    public static final EventCancelled EventCancelled;
    public static final EventCancelled EventNew;
    public static final EventCancelled VALUES[] = values();

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatYesNoEvent$EventState, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        EventNew = new <init>("EventNew", 0);
        EventAccepted = new <init>("EventAccepted", 1);
        EventCancelled = new <init>("EventCancelled", 2);
        $VALUES = (new .VALUES[] {
            EventNew, EventAccepted, EventCancelled
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
