package com.lumiyaviewer.lumiya.slproto.modules.xfer;

public enum ELLPath {
    LL_PATH_NONE(0),
    LL_PATH_USER_SETTINGS(1),
    LL_PATH_APP_SETTINGS(2),
    LL_PATH_PER_SL_ACCOUNT(3),
    LL_PATH_CACHE(4),
    LL_PATH_CHARACTER(5),
    LL_PATH_HELP(6),
    LL_PATH_LOGS(7),
    LL_PATH_TEMP(8),
    LL_PATH_SKINS(9),
    LL_PATH_TOP_SKIN(10),
    LL_PATH_CHAT_LOGS(11),
    LL_PATH_PER_ACCOUNT_CHAT_LOGS(12),
    LL_PATH_USER_SKIN(14),
    LL_PATH_LOCAL_ASSETS(15),
    LL_PATH_EXECUTABLE(16),
    LL_PATH_DEFAULT_SKIN(17),
    LL_PATH_FONTS(18);
    
    private int code;

    private ELLPath(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }
}
