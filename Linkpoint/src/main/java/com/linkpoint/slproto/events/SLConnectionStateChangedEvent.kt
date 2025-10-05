package com.linkpoint.slproto.events

import com.linkpoint.slproto.SLGridConnection

data class SLConnectionStateChangedEvent(
    val connectionState: SLGridConnection.ConnectionState
)