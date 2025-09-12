// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;


public final class ChatterListType extends Enum
{

    private static final ChatterListType $VALUES[];
    public static final ChatterListType Active;
    public static final ChatterListType Friends;
    public static final ChatterListType FriendsOnline;
    public static final ChatterListType Groups;
    public static final ChatterListType Nearby;

    private ChatterListType(String s, int i)
    {
        super(s, i);
    }

    public static ChatterListType valueOf(String s)
    {
        return (ChatterListType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/users/manager/ChatterListType, s);
    }

    public static ChatterListType[] values()
    {
        return $VALUES;
    }

    static 
    {
        FriendsOnline = new ChatterListType("FriendsOnline", 0);
        Friends = new ChatterListType("Friends", 1);
        Active = new ChatterListType("Active", 2);
        Groups = new ChatterListType("Groups", 3);
        Nearby = new ChatterListType("Nearby", 4);
        $VALUES = (new ChatterListType[] {
            FriendsOnline, Friends, Active, Groups, Nearby
        });
    }
}
