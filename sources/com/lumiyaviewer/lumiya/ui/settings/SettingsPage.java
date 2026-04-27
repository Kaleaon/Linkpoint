// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;


public final class SettingsPage extends Enum
{

    private static final SettingsPage $VALUES[];
    public static final SettingsPage Page3D;
    public static final SettingsPage PageAppearance;
    public static final SettingsPage PageCache;
    public static final SettingsPage PageChat;
    public static final SettingsPage PageConnection;
    public static final SettingsPage PageNotifications;
    public static final SettingsPage PageRLV;
    private final int pageResourceId;
    private final int pageTitle;

    private SettingsPage(String s, int i, int j, int k)
    {
        super(s, i);
        pageResourceId = j;
        pageTitle = k;
    }

    public static SettingsPage valueOf(String s)
    {
        return (SettingsPage)Enum.valueOf(com/lumiyaviewer/lumiya/ui/settings/SettingsPage, s);
    }

    public static SettingsPage[] values()
    {
        return $VALUES;
    }

    public int getPageResourceId()
    {
        return pageResourceId;
    }

    public int getPageTitle()
    {
        return pageTitle;
    }

    static 
    {
        PageConnection = new SettingsPage("PageConnection", 0, 0x7f070009, 0x7f090283);
        PageAppearance = new SettingsPage("PageAppearance", 1, 0x7f070006, 0x7f090280);
        PageChat = new SettingsPage("PageChat", 2, 0x7f070008, 0x7f090282);
        PageNotifications = new SettingsPage("PageNotifications", 3, 0x7f07000a, 0x7f090284);
        Page3D = new SettingsPage("Page3D", 4, 0x7f070003, 0x7f09027e);
        PageRLV = new SettingsPage("PageRLV", 5, 0x7f07000e, 0x7f090288);
        PageCache = new SettingsPage("PageCache", 6, 0x7f070007, 0x7f090281);
        $VALUES = (new SettingsPage[] {
            PageConnection, PageAppearance, PageChat, PageNotifications, Page3D, PageRLV, PageCache
        });
    }
}
