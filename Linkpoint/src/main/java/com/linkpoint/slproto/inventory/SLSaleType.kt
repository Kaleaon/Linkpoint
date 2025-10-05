package com.linkpoint.slproto.inventory

import android.support.v4.os.EnvironmentCompat

enum class SLSaleType(val typeCode: Int, val stringCode: String) {
    FS_NOT(0, "not"),
    FS_ORIGINAL(1, "orig"),
    FS_COPY(2, "copy"),
    FS_CONTENTS(3, "cntn"),
    FS_UNKNOWN(-1, EnvironmentCompat.MEDIA_UNKNOWN);

    companion object {
        @JvmStatic
        fun getByString(str: String): SLSaleType {
            return values().firstOrNull { it.stringCode.equals(str, ignoreCase = true) } ?: FS_UNKNOWN
        }

        @JvmStatic
        fun getByType(typeCode: Int): SLSaleType {
            return values().firstOrNull { it.typeCode == typeCode } ?: FS_UNKNOWN
        }
    }
}