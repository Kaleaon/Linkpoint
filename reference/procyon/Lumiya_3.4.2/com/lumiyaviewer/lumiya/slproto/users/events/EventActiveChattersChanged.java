// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventActiveChattersChanged
{
    public final UUID agentUUID;
    
    public EventActiveChattersChanged(final UUID agentUUID) {
        this.agentUUID = agentUUID;
    }
}
