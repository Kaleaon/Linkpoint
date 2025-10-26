package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.manager.GroupManager
import java.util.UUID

final class AutoValue_GroupManager_GroupMembersQuery : GroupManager().GroupMembersQuery {
    private val UUID groupID
    private val UUID requestID

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

     public fun equals(obj: Object): Boolean {
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

     public fun groupID(): UUID {
        return this.groupID
    }

     public fun hashCode(): Int {
        return ((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.requestID.hashCode()
    }

     public fun requestID(): UUID {
        return this.requestID
    }

     public fun toString(): String {
        return "GroupMembersQuery{groupID=" + this.groupID + ", " + "requestID=" + this.requestID + "}"
    }
}
