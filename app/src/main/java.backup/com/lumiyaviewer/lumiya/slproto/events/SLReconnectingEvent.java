package com.lumiyaviewer.lumiya.slproto.events;

public class SLReconnectingEvent {
    public final int attempt;

    public SLReconnectingEvent(int i) {
        this.attempt = i;
    }
}
