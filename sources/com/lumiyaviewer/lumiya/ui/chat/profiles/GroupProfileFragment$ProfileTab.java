// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupProfileFragment, GroupMainProfileTab, GroupRolesProfileTab, GroupMembersProfileTab

private static final class tabClass extends Enum
{

    private static final Members $VALUES[];
    public static final Members MainProfile;
    public static final Members Members;
    public static final Members Roles;
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
        return (tabClass)Enum.valueOf(com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment$ProfileTab, s);
    }

    public static tabClass[] values()
    {
        return $VALUES;
    }

    static 
    {
        MainProfile = new <init>("MainProfile", 0, 0x7f090297, com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMainProfileTab);
        Roles = new <init>("Roles", 1, 0x7f090152, com/lumiyaviewer/lumiya/ui/chat/profiles/GroupRolesProfileTab);
        Members = new <init>("Members", 2, 0x7f090144, com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMembersProfileTab);
        $VALUES = (new .VALUES[] {
            MainProfile, Roles, Members
        });
    }

    private (String s, int i, int j, Class class1)
    {
        super(s, i);
        tabCaption = j;
        tabClass = class1;
    }
}
