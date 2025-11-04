package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

/* loaded from: classes.dex */
public class EventActiveChattersChanged {
    public final UUID agentUUID;

    public EventActiveChattersChanged(UUID uuid) {
        this.agentUUID = uuid;
    }
}
