package com.linkpoint.slproto.events

import java.util.UUID

data class SLLoginResultEvent(
    val success: Boolean,
    val message: String,
    val activeAgentUUID: UUID
)