package com.lumiyaviewer.lumiya.slproto.events;

public class SLDisconnectEvent {
    public final String message;
    public final boolean normalDisconnect;

    public SLDisconnectEvent(boolean z, String str) {
        this.normalDisconnect = z;
        this.message = str;
    }
}
