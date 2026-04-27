package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public class SLChatEventUpdatedEvent {
    public final SLChatEvent chatEvent;

    public SLChatEventUpdatedEvent(SLChatEvent sLChatEvent) {
        this.chatEvent = sLChatEvent;
    }
}
