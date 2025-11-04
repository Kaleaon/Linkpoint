// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLLoginResultEvent
{
    public final UUID activeAgentUUID;
    public final String message;
    public final boolean success;
    
    public SLLoginResultEvent(final boolean success, final String message, final UUID activeAgentUUID) {
        this.success = success;
        this.message = message;
        this.activeAgentUUID = activeAgentUUID;
    }
}
