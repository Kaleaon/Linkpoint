// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public class SLChatEventUpdatedEvent
{
    public final SLChatEvent chatEvent;
    
    public SLChatEventUpdatedEvent(final SLChatEvent chatEvent) {
        this.chatEvent = chatEvent;
    }
}
