package com.lumiyaviewer.lumiya.slproto.events

import java.util.UUID

data class SLJoinLeaveGroupEvent(
    val groupID: UUID,
    val isJoin: Boolean,
    val success: Boolean,
)
