package com.lumiyaviewer.lumiya.dao

import java.util.UUID

data class Chatter(
    var id: Long? = null,
    var type: Int = 0,
    var uuid: UUID? = null,
    var active: Boolean = false,
    var muted: Boolean = false,
    var unreadCount: Int = 0,
    var lastMessageID: Long? = null,
    var lastSessionID: UUID? = null
)