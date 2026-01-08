package com.linkpoint.slproto.modules.mutelist

enum class MuteType(val viewOrder: Int) {
    BY_NAME(2),
    AGENT(0),
    OBJECT(1),
    GROUP(3),
    EXTERNAL(4),
}