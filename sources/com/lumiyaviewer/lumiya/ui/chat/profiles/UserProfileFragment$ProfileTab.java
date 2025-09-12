// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserProfileFragment, UserMainProfileTab, UserPicksProfileTab, UserGroupsProfileTab, 
//            UserFirstLifeProfileTab

private static final class tabClass extends Enum
{

    private static final FirstLife $VALUES[];
    public static final FirstLife FirstLife;
    public static final FirstLife Groups;
    public static final FirstLife MainProfile;
    public static final FirstLife Picks;
    private final int tabCaption;
    private final Class tabClass;

    static int _2D_get0(tabClass tabclass)
    {
        return tabclass.tabCaption;
    }

    static Class _2D_get1(tabCaption tabcaption)
    {
        return tabcaption.tabClass;
    }

    public static tabClass valueOf(String s)
    {
        return (tabClass)Enum.valueOf(com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment$ProfileTab, s);
    }

    public static tabClass[] values()
    {
        return $VALUES;
    }

    static 
    {
        MainProfile = new <init>("MainProfile", 0, 0x7f090297, com/lumiyaviewer/lumiya/ui/chat/profiles/UserMainProfileTab);
        Picks = new <init>("Picks", 1, 0x7f090296, com/lumiyaviewer/lumiya/ui/chat/profiles/UserPicksProfileTab);
        Groups = new <init>("Groups", 2, 0x7f090290, com/lumiyaviewer/lumiya/ui/chat/profiles/UserGroupsProfileTab);
        FirstLife = new <init>("FirstLife", 3, 0x7f09028a, com/lumiyaviewer/lumiya/ui/chat/profiles/UserFirstLifeProfileTab);
        $VALUES = (new .VALUES[] {
            MainProfile, Picks, Groups, FirstLife
        });
    }

    private (String s, int i, int j, Class class1)
    {
        super(s, i);
        tabCaption = j;
        tabClass = class1;
    }
}
