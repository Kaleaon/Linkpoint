// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;


final class MyAvatarDetailsPages extends Enum
{

    private static final MyAvatarDetailsPages $VALUES[];
    public static final MyAvatarDetailsPages pageBalance;
    public static final MyAvatarDetailsPages pageBlockList;
    public static final MyAvatarDetailsPages pageOutfits;
    public static final MyAvatarDetailsPages pageProfile;
    private final int titleResource;

    private MyAvatarDetailsPages(String s, int i, int j)
    {
        super(s, i);
        titleResource = j;
    }

    public static MyAvatarDetailsPages valueOf(String s)
    {
        return (MyAvatarDetailsPages)Enum.valueOf(com/lumiyaviewer/lumiya/ui/myava/MyAvatarDetailsPages, s);
    }

    public static MyAvatarDetailsPages[] values()
    {
        return $VALUES;
    }

    public int getTitleResource()
    {
        return titleResource;
    }

    static 
    {
        pageProfile = new MyAvatarDetailsPages("pageProfile", 0, 0x7f0901c7);
        pageOutfits = new MyAvatarDetailsPages("pageOutfits", 1, 0x7f0901c6);
        pageBlockList = new MyAvatarDetailsPages("pageBlockList", 2, 0x7f090080);
        pageBalance = new MyAvatarDetailsPages("pageBalance", 3, -1);
        $VALUES = (new MyAvatarDetailsPages[] {
            pageProfile, pageOutfits, pageBlockList, pageBalance
        });
    }
}
