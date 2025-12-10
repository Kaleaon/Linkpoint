package com.linkpoint.slproto.inventory

import androidx.v4.os.EnvironmentCompat

enum SLSaleType {
    FS_NOT(0, "not"),
    FS_ORIGINAL(1, "orig"),
    FS_COPY(2, "copy"),
    FS_CONTENTS(3, "cntn"),
    FS_UNKNOWN(-1, EnvironmentCompat.MEDIA_UNKNOWN)
    
    private String stringCode
    private Int typeCode

    private SLSaleType(Int i, String str) {
        this.typeCode = i
        this.stringCode = str
    }

    fun getByString(String str): SLSaleType {
        for (SLSaleType sLSaleType : values()) {
            if (sLSaleType.stringCode.equalsIgnoreCase(str)) {
                return sLSaleType
            }
        }
        return FS_UNKNOWN
    }

    fun getByType(Int i): SLSaleType {
        for (SLSaleType sLSaleType : values()) {
            if (sLSaleType.typeCode == i) {
                return sLSaleType
            }
        }
        return FS_UNKNOWN
    }

    fun getStringCode(): String {
        return this.stringCode
    }

    fun getTypeCode(): Int {
        return this.typeCode
    }
}
