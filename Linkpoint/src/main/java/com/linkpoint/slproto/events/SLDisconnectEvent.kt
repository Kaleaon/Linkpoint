package com.linkpoint.slproto.events

data class SLDisconnectEvent(
    val normalDisconnect: Boolean,
    val message: String
)