package com.lumiyaviewer.lumiya.slproto.inventory;

import android.support.v4.os.EnvironmentCompat;

public enum SLSaleType {
    FS_NOT(0, "not"),
    FS_ORIGINAL(1, "orig"),
    FS_COPY(2, "copy"),
    FS_CONTENTS(3, "cntn"),
    FS_UNKNOWN(-1, EnvironmentCompat.MEDIA_UNKNOWN);
    
    private String stringCode;
    private int typeCode;

    private SLSaleType(int i, String str) {
        this.typeCode = i;
        this.stringCode = str;
    }

    public static SLSaleType getByString(String str) {
        for (SLSaleType sLSaleType : values()) {
            if (sLSaleType.stringCode.equalsIgnoreCase(str)) {
                return sLSaleType;
            }
        }
        return FS_UNKNOWN;
    }

    public static SLSaleType getByType(int i) {
        for (SLSaleType sLSaleType : values()) {
            if (sLSaleType.typeCode == i) {
                return sLSaleType;
            }
        }
        return FS_UNKNOWN;
    }

    public String getStringCode() {
        return this.stringCode;
    }

    public int getTypeCode() {
        return this.typeCode;
    }
}
