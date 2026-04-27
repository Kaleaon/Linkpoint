package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventUserOnlineStatusChanged {
    public final UUID agentUUID;
    public final boolean isOnline;
    public final UUID userUUID;

    public EventUserOnlineStatusChanged(UUID uuid, UUID uuid2, boolean z) {
        this.agentUUID = uuid;
        this.userUUID = uuid2;
        this.isOnline = z;
    }
}
