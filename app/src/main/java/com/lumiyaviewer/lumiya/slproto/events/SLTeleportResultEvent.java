package com.lumiyaviewer.lumiya.slproto.events;

public class SLTeleportResultEvent {
    public String message;
    public boolean success;

    public SLTeleportResultEvent(boolean z, String str) {
        this.success = z;
        this.message = str;
    }
}
