// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.SLGridConnection;

public class SLConnectionStateChangedEvent
{
    public final SLGridConnection.ConnectionState connectionState;
    
    public SLConnectionStateChangedEvent(final SLGridConnection.ConnectionState connectionState) {
        this.connectionState = connectionState;
    }
}
