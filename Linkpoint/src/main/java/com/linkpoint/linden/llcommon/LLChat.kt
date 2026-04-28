package com.linkpoint.linden.llcommon

enum class EChatType(val value: Int) {
    WHISPER(0),
    NORMAL(1),
    SHOUT(2),
    START(4),
    STOP(5),
    DEBUG_MSG(6),
    REGION(7),
    OWNER(8),
    DIRECT(9),
    UNKNOWN(-1);

    companion object {
        fun fromInt(v: Int): EChatType = entries.firstOrNull { it.value == v } ?: UNKNOWN
    }
}

enum class EChatSourceType(val value: Int) {
    SYSTEM(0),
    AGENT(1),
    OBJECT(2),
    UNKNOWN(3);

    companion object {
        fun fromInt(v: Int): EChatSourceType = entries.firstOrNull { it.value == v } ?: UNKNOWN
    }
}

enum class EChatAudibleLevel(val value: Int) {
    NOT(-1),
    BARELY(0),
    FULLY(1);

    companion object {
        fun fromInt(v: Int): EChatAudibleLevel = entries.firstOrNull { it.value == v } ?: BARELY
    }
}
