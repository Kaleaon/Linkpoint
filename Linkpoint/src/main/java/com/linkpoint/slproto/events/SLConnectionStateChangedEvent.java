package com.linkpoint.slproto.events;

import com.linkpoint.slproto.SLGridConnection;

public class SLConnectionStateChangedEvent {
    public final SLGridConnection.ConnectionState connectionState;

    public SLConnectionStateChangedEvent(SLGridConnection.ConnectionState connectionState2) {
        this.connectionState = connectionState2;
    }
}
