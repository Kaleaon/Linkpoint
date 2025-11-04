package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

/* loaded from: classes.dex */
public class SLLoginResultEvent {
    public final UUID activeAgentUUID;
    public final String message;
    public final boolean success;

    public SLLoginResultEvent(boolean z, String str, UUID uuid) {
        this.success = z;
        this.message = str;
        this.activeAgentUUID = uuid;
    }
}
