// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

public class SLDisconnectEvent
{
    public final String message;
    public final boolean normalDisconnect;
    
    public SLDisconnectEvent(final boolean normalDisconnect, final String message) {
        this.normalDisconnect = normalDisconnect;
        this.message = message;
    }
}
