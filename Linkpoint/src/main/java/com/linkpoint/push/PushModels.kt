package com.linkpoint.push

import java.util.UUID

enum class PushEventType {
    IM,
    GROUP_NOTICE,
    UNKNOWN
}

data class PushEvent(
    val dedupeId: String,
    val type: PushEventType,
    val sessionId: UUID?,
    val senderId: UUID?,
    val senderName: String?,
    val title: String,
    val message: String,
    val timestampMs: Long
)
