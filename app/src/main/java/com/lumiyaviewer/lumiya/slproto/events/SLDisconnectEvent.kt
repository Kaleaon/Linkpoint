package com.lumiyaviewer.lumiya.slproto.events

data class SLDisconnectEvent(
    val normalDisconnect: Boolean,
    val message: String,
)
