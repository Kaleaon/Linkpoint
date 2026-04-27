package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.SLGridConnection;

public class SLConnectionStateChangedEvent {
    public final SLGridConnection.ConnectionState connectionState;

    public SLConnectionStateChangedEvent(SLGridConnection.ConnectionState connectionState2) {
        this.connectionState = connectionState2;
    }
}
