package com.linkpoint.linden.llmessage

object ClassifiedFlags {
    const val NONE: UByte = 1u        // 1 << 0
    const val MATURE: UByte = 2u      // 1 << 1
    const val UPDATE_TIME: UByte = 16u // 1 << 4
    const val AUTO_RENEW: UByte = 32u  // 1 << 5

    const val QUERY_FILTER_MATURE: UByte = 2u   // 1 << 1
    const val QUERY_INC_PG: UByte = 4u          // 1 << 2
    const val QUERY_INC_MATURE: UByte = 8u      // 1 << 3
    const val QUERY_INC_ADULT: UByte = 64u      // 1 << 6
    val QUERY_INC_NEW_VIEWER: UByte = (QUERY_INC_PG or QUERY_INC_MATURE or QUERY_INC_ADULT)

    const val MAX_CLASSIFIEDS: Int = 100

    fun isAutoRenew(flags: UByte): Boolean = (flags and AUTO_RENEW) != 0u.toUByte()
    fun isMature(flags: UByte): Boolean = (flags and MATURE) != 0u.toUByte()
    fun isUpdateTime(flags: UByte): Boolean = (flags and UPDATE_TIME) != 0u.toUByte()
    fun isEnabled(flags: UByte): Boolean = (flags and NONE) != 0u.toUByte()

    fun pack(autoRenew: Boolean, isPg: Boolean, isMature: Boolean, isAdult: Boolean): UByte {
        var flags: UByte = 0u
        if (autoRenew) flags = flags or AUTO_RENEW
        if (isMature) flags = flags or MATURE
        if (isPg) flags = flags or QUERY_INC_PG
        if (isAdult) flags = flags or QUERY_INC_ADULT
        return flags
    }

    fun packRequest(autoRenew: Boolean, isPg: Boolean, isMature: Boolean, isAdult: Boolean): UByte {
        var flags: UByte = 0u
        if (autoRenew) flags = flags or AUTO_RENEW
        if (isMature) flags = flags or QUERY_FILTER_MATURE
        if (isPg) flags = flags or QUERY_INC_PG
        if (isAdult) flags = flags or QUERY_INC_ADULT
        return flags
    }
}
