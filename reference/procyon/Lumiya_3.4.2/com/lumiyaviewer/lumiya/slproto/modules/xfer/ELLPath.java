// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;

public enum ELLPath
{
    LL_PATH_APP_SETTINGS("LL_PATH_APP_SETTINGS", 2, 2), 
    LL_PATH_CACHE("LL_PATH_CACHE", 4, 4), 
    LL_PATH_CHARACTER("LL_PATH_CHARACTER", 5, 5), 
    LL_PATH_CHAT_LOGS("LL_PATH_CHAT_LOGS", 11, 11), 
    LL_PATH_DEFAULT_SKIN("LL_PATH_DEFAULT_SKIN", 16, 17), 
    LL_PATH_EXECUTABLE("LL_PATH_EXECUTABLE", 15, 16), 
    LL_PATH_FONTS("LL_PATH_FONTS", 17, 18), 
    LL_PATH_HELP("LL_PATH_HELP", 6, 6), 
    LL_PATH_LOCAL_ASSETS("LL_PATH_LOCAL_ASSETS", 14, 15), 
    LL_PATH_LOGS("LL_PATH_LOGS", 7, 7), 
    LL_PATH_NONE("LL_PATH_NONE", 0, 0), 
    LL_PATH_PER_ACCOUNT_CHAT_LOGS("LL_PATH_PER_ACCOUNT_CHAT_LOGS", 12, 12), 
    LL_PATH_PER_SL_ACCOUNT("LL_PATH_PER_SL_ACCOUNT", 3, 3), 
    LL_PATH_SKINS("LL_PATH_SKINS", 9, 9), 
    LL_PATH_TEMP("LL_PATH_TEMP", 8, 8), 
    LL_PATH_TOP_SKIN("LL_PATH_TOP_SKIN", 10, 10), 
    LL_PATH_USER_SETTINGS("LL_PATH_USER_SETTINGS", 1, 1), 
    LL_PATH_USER_SKIN("LL_PATH_USER_SKIN", 13, 14);
    
    private int code;
    
    private ELLPath(final String name, final int ordinal, final int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
}
