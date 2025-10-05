package com.linkpoint.slproto.events;

import com.linkpoint.slproto.chat.generic.SLChatEvent;

public class SLChatEventUpdatedEvent {
    public final SLChatEvent chatEvent;

    public SLChatEventUpdatedEvent(SLChatEvent sLChatEvent) {
        this.chatEvent = sLChatEvent;
    }
}
