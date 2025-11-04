// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

public class SLBakingProgressEvent
{
    public boolean done;
    public boolean first;
    public int progress;
    
    public SLBakingProgressEvent(final boolean first, final boolean done, final int progress) {
        this.first = first;
        this.done = done;
        this.progress = progress;
    }
}
