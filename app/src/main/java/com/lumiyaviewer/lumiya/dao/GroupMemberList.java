package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupMemberList {
    private UUID groupID;
    private UUID requestID;

    public GroupMemberList(UUID uuid) {
        this.groupID = uuid;
    }

    public GroupMemberList(UUID uuid, UUID uuid2) {
        this.groupID = uuid;
        this.requestID = uuid2;
    }

    public UUID getGroupID() {
        return this.groupID;
    }

    public UUID getRequestID() {
        return this.requestID;
    }

    public void setGroupID(UUID uuid) {
        this.groupID = uuid;
    }

    public void setRequestID(UUID uuid) {
        this.requestID = uuid;
    }
}
