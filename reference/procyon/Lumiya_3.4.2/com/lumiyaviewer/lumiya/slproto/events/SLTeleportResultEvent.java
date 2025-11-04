// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

public class SLTeleportResultEvent
{
    public String message;
    public boolean success;
    
    public SLTeleportResultEvent(final boolean success, final String message) {
        this.success = success;
        this.message = message;
    }
}
