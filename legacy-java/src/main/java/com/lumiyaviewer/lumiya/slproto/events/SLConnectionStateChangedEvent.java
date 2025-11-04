package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.SLGridConnection;

/* loaded from: classes.dex */
public class SLConnectionStateChangedEvent {
    public final SLGridConnection.ConnectionState connectionState;

    public SLConnectionStateChangedEvent(SLGridConnection.ConnectionState connectionState) {
        this.connectionState = connectionState;
    }
}
