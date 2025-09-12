// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.chatsrc:
//            ChatMessageSource

public static final class  extends Enum
{

    private static final Object $VALUES[];
    public static final Object Group;
    public static final Object Object;
    public static final Object System;
    public static final Object Unknown;
    public static final Object User;
    public static final Object VALUES[] = values();

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/users/chatsrc/ChatMessageSource$ChatMessageSourceType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Unknown = new <init>("Unknown", 0);
        System = new <init>("System", 1);
        User = new <init>("User", 2);
        Group = new <init>("Group", 3);
        Object = new <init>("Object", 4);
        $VALUES = (new .VALUES[] {
            Unknown, System, User, Group, Object
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
