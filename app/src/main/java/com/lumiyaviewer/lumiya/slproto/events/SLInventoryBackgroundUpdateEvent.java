package com.lumiyaviewer.lumiya.slproto.events;

public class SLInventoryBackgroundUpdateEvent {
    public final boolean backgroundUpdateActive;

    public SLInventoryBackgroundUpdateEvent(boolean z) {
        this.backgroundUpdateActive = z;
    }
}
