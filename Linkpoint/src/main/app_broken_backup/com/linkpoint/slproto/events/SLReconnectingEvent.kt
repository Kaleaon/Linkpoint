package com.linkpoint.slproto.events

/**
 * Event fired when reconnecting to Second Life
 */
data class SLReconnectingEvent(
    val attempt: Int,
)
