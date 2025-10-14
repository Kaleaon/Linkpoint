package com.lumiyaviewer.lumiya.slproto.events

import com.lumiyaviewer.lumiya.slproto.SLGridConnection

data class SLConnectionStateChangedEvent(
    val connectionState: SLGridConnection.ConnectionState,
)
