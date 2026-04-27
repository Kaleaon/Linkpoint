// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ContactsFragment

private static final class  extends Enum
{

    private static final Nearby $VALUES[];
    public static final Nearby Active;
    public static final Nearby Friends;
    public static final Nearby Groups;
    public static final Nearby Nearby;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/ui/chat/ContactsFragment$ContactListType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Active = new <init>("Active", 0);
        Friends = new <init>("Friends", 1);
        Groups = new <init>("Groups", 2);
        Nearby = new <init>("Nearby", 3);
        $VALUES = (new .VALUES[] {
            Active, Friends, Groups, Nearby
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
