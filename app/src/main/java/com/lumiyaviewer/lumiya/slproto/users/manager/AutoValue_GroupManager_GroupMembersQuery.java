package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import java.util.UUID;

final class AutoValue_GroupManager_GroupMembersQuery extends GroupManager.GroupMembersQuery {
    private final UUID groupID;
    private final UUID requestID;

    AutoValue_GroupManager_GroupMembersQuery(UUID uuid, UUID uuid2) {
        if (uuid == null) {
            throw new NullPointerException("Null groupID");
        }
        this.groupID = uuid;
        if (uuid2 == null) {
            throw new NullPointerException("Null requestID");
        }
        this.requestID = uuid2;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GroupManager.GroupMembersQuery)) {
            return false;
        }
        GroupManager.GroupMembersQuery groupMembersQuery = (GroupManager.GroupMembersQuery) obj;
        if (this.groupID.equals(groupMembersQuery.groupID())) {
            return this.requestID.equals(groupMembersQuery.requestID());
        }
        return false;
    }

    public UUID groupID() {
        return this.groupID;
    }

    public int hashCode() {
        return ((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.requestID.hashCode();
    }

    public UUID requestID() {
        return this.requestID;
    }

    public String toString() {
        return "GroupMembersQuery{groupID=" + this.groupID + ", " + "requestID=" + this.requestID + "}";
    }
}
