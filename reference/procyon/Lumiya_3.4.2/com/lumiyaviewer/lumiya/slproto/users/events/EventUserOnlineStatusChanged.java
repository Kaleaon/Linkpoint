// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventUserOnlineStatusChanged
{
    public final UUID agentUUID;
    public final boolean isOnline;
    public final UUID userUUID;
    
    public EventUserOnlineStatusChanged(final UUID agentUUID, final UUID userUUID, final boolean isOnline) {
        this.agentUUID = agentUUID;
        this.userUUID = userUUID;
        this.isOnline = isOnline;
    }
}
