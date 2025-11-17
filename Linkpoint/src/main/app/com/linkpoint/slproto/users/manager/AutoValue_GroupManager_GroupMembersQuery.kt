package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.manager.GroupManager
import java.util.UUID

class AutoValue_GroupManager_GroupMembersQuery : GroupManager.GroupMembersQuery {
    private UUID groupID
    private UUID requestID

    AutoValue_GroupManager_GroupMembersQuery(UUID uuid, UUID uuid2) {
        if (uuid == null) {
            throw NullPointerException("Null groupID")
        }
        this.groupID = uuid
        if (uuid2 == null) {
            throw NullPointerException("Null requestID")
        }
        this.requestID = uuid2
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof GroupManager.GroupMembersQuery)) {
            return false
        }
        GroupManager.GroupMembersQuery groupMembersQuery = (GroupManager.GroupMembersQuery) obj
        if (this.groupID.equals(groupMembersQuery.groupID())) {
            return this.requestID.equals(groupMembersQuery.requestID())
        }
        return false
    }

    UUID groupID() {
        return this.groupID
    }

    Int hashCode() {
        return ((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.requestID.hashCode()
    }

    UUID requestID() {
        return this.requestID
    }

    String toString() {
        return "GroupMembersQuery{groupID=" + this.groupID + ", " + "requestID=" + this.requestID + "}"
    }
}
