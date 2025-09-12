// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;


public final class ELLPath extends Enum
{

    private static final ELLPath $VALUES[];
    public static final ELLPath LL_PATH_APP_SETTINGS;
    public static final ELLPath LL_PATH_CACHE;
    public static final ELLPath LL_PATH_CHARACTER;
    public static final ELLPath LL_PATH_CHAT_LOGS;
    public static final ELLPath LL_PATH_DEFAULT_SKIN;
    public static final ELLPath LL_PATH_EXECUTABLE;
    public static final ELLPath LL_PATH_FONTS;
    public static final ELLPath LL_PATH_HELP;
    public static final ELLPath LL_PATH_LOCAL_ASSETS;
    public static final ELLPath LL_PATH_LOGS;
    public static final ELLPath LL_PATH_NONE;
    public static final ELLPath LL_PATH_PER_ACCOUNT_CHAT_LOGS;
    public static final ELLPath LL_PATH_PER_SL_ACCOUNT;
    public static final ELLPath LL_PATH_SKINS;
    public static final ELLPath LL_PATH_TEMP;
    public static final ELLPath LL_PATH_TOP_SKIN;
    public static final ELLPath LL_PATH_USER_SETTINGS;
    public static final ELLPath LL_PATH_USER_SKIN;
    private int code;

    private ELLPath(String s, int i, int j)
    {
        super(s, i);
        code = j;
    }

    public static ELLPath valueOf(String s)
    {
        return (ELLPath)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/xfer/ELLPath, s);
    }

    public static ELLPath[] values()
    {
        return $VALUES;
    }

    public int getCode()
    {
        return code;
    }

    static 
    {
        LL_PATH_NONE = new ELLPath("LL_PATH_NONE", 0, 0);
        LL_PATH_USER_SETTINGS = new ELLPath("LL_PATH_USER_SETTINGS", 1, 1);
        LL_PATH_APP_SETTINGS = new ELLPath("LL_PATH_APP_SETTINGS", 2, 2);
        LL_PATH_PER_SL_ACCOUNT = new ELLPath("LL_PATH_PER_SL_ACCOUNT", 3, 3);
        LL_PATH_CACHE = new ELLPath("LL_PATH_CACHE", 4, 4);
        LL_PATH_CHARACTER = new ELLPath("LL_PATH_CHARACTER", 5, 5);
        LL_PATH_HELP = new ELLPath("LL_PATH_HELP", 6, 6);
        LL_PATH_LOGS = new ELLPath("LL_PATH_LOGS", 7, 7);
        LL_PATH_TEMP = new ELLPath("LL_PATH_TEMP", 8, 8);
        LL_PATH_SKINS = new ELLPath("LL_PATH_SKINS", 9, 9);
        LL_PATH_TOP_SKIN = new ELLPath("LL_PATH_TOP_SKIN", 10, 10);
        LL_PATH_CHAT_LOGS = new ELLPath("LL_PATH_CHAT_LOGS", 11, 11);
        LL_PATH_PER_ACCOUNT_CHAT_LOGS = new ELLPath("LL_PATH_PER_ACCOUNT_CHAT_LOGS", 12, 12);
        LL_PATH_USER_SKIN = new ELLPath("LL_PATH_USER_SKIN", 13, 14);
        LL_PATH_LOCAL_ASSETS = new ELLPath("LL_PATH_LOCAL_ASSETS", 14, 15);
        LL_PATH_EXECUTABLE = new ELLPath("LL_PATH_EXECUTABLE", 15, 16);
        LL_PATH_DEFAULT_SKIN = new ELLPath("LL_PATH_DEFAULT_SKIN", 16, 17);
        LL_PATH_FONTS = new ELLPath("LL_PATH_FONTS", 17, 18);
        $VALUES = (new ELLPath[] {
            LL_PATH_NONE, LL_PATH_USER_SETTINGS, LL_PATH_APP_SETTINGS, LL_PATH_PER_SL_ACCOUNT, LL_PATH_CACHE, LL_PATH_CHARACTER, LL_PATH_HELP, LL_PATH_LOGS, LL_PATH_TEMP, LL_PATH_SKINS, 
            LL_PATH_TOP_SKIN, LL_PATH_CHAT_LOGS, LL_PATH_PER_ACCOUNT_CHAT_LOGS, LL_PATH_USER_SKIN, LL_PATH_LOCAL_ASSETS, LL_PATH_EXECUTABLE, LL_PATH_DEFAULT_SKIN, LL_PATH_FONTS
        });
    }
}
