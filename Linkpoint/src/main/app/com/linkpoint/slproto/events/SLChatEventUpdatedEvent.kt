package com.linkpoint.slproto.events

import com.linkpoint.slproto.chat.generic.SLChatEvent

data class SLChatEventUpdatedEvent(
    val chatEvent: SLChatEvent,
)
