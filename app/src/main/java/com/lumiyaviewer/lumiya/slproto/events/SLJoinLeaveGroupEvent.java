package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLJoinLeaveGroupEvent {
    public final UUID groupID;
    public final boolean isJoin;
    public final boolean success;

    public SLJoinLeaveGroupEvent(UUID uuid, boolean z, boolean z2) {
        this.groupID = uuid;
        this.isJoin = z;
        this.success = z2;
    }
}
