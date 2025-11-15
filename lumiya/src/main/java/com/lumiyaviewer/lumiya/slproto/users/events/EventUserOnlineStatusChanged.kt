package com.lumiyaviewer.lumiya.slproto.users.events

import java.util.UUID

data class EventUserOnlineStatusChanged(
    val agentUUID: UUID,
    val userUUID: UUID,
    val isOnline: Boolean,
)
