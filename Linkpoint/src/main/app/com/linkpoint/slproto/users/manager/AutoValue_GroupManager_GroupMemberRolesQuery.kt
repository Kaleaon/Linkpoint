package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.manager.GroupManager
import java.util.UUID

class AutoValue_GroupManager_GroupMemberRolesQuery : GroupManager.GroupMemberRolesQuery {
    private UUID groupID
    private UUID memberID
    private UUID requestID

    AutoValue_GroupManager_GroupMemberRolesQuery(UUID uuid, UUID uuid2, UUID uuid3) {
        if (uuid == null) {
            throw NullPointerException("Null groupID")
        }
        this.groupID = uuid
        if (uuid2 == null) {
            throw NullPointerException("Null memberID")
        }
        this.memberID = uuid2
        if (uuid3 == null) {
            throw NullPointerException("Null requestID")
        }
        this.requestID = uuid3
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof GroupManager.GroupMemberRolesQuery)) {
            return false
        }
        GroupManager.GroupMemberRolesQuery groupMemberRolesQuery = (GroupManager.GroupMemberRolesQuery) obj
        if (!this.groupID.equals(groupMemberRolesQuery.groupID()) || !this.memberID.equals(groupMemberRolesQuery.memberID())) {
            return false
        }
        return this.requestID.equals(groupMemberRolesQuery.requestID())
    }

    fun groupID(): UUID {
        return this.groupID
    }

    fun hashCode(): Int {
        return ((((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.memberID.hashCode()) * 1000003) ^ this.requestID.hashCode()
    }

    fun memberID(): UUID {
        return this.memberID
    }

    fun requestID(): UUID {
        return this.requestID
    }

    fun toString(): String {
        return "GroupMemberRolesQuery{groupID=" + this.groupID + ", " + "memberID=" + this.memberID + ", " + "requestID=" + this.requestID + "}"
    }
}
