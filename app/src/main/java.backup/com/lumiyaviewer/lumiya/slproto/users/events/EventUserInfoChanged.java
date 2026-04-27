package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventUserInfoChanged {
    public static final int CHANGED_NAME = 2;
    public static final int CHANGED_PROFILE = 4;
    public final UUID agentUUID;
    public final int changedMask;
    public final UUID userUUID;

    public EventUserInfoChanged(UUID uuid, UUID uuid2, int i) {
        this.agentUUID = uuid;
        this.userUUID = uuid2;
        this.changedMask = i;
    }

    public boolean isNameChanged() {
        return (this.changedMask & 2) != 0;
    }

    public boolean isProfileChanged() {
        return (this.changedMask & 4) != 0;
    }
}
