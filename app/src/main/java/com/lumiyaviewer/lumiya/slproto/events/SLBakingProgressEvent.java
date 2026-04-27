package com.lumiyaviewer.lumiya.slproto.events;

public class SLBakingProgressEvent {
    public boolean done;
    public boolean first;
    public int progress;

    public SLBakingProgressEvent(boolean z, boolean z2, int i) {
        this.first = z;
        this.done = z2;
        this.progress = i;
    }
}
