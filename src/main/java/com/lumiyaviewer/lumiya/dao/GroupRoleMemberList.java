package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupRoleMemberList {
    private UUID groupID;
    private boolean mustRevalidate;
    private UUID requestID;

    public GroupRoleMemberList(UUID uuid) {
        this.groupID = uuid;
    }

    public GroupRoleMemberList(UUID uuid, UUID uuid2, boolean z) {
        this.groupID = uuid;
        this.requestID = uuid2;
        this.mustRevalidate = z;
    }

    public UUID getGroupID() {
        return this.groupID;
    }

    public boolean getMustRevalidate() {
        return this.mustRevalidate;
    }

    public UUID getRequestID() {
        return this.requestID;
    }

    public void setGroupID(UUID uuid) {
        this.groupID = uuid;
    }

    public void setMustRevalidate(boolean z) {
        this.mustRevalidate = z;
    }

    public void setRequestID(UUID uuid) {
        this.requestID = uuid;
    }
}
