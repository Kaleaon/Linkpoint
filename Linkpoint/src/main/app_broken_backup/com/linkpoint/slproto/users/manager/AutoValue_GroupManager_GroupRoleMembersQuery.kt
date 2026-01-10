package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.manager.GroupManager
import java.util.UUID

class AutoValue_GroupManager_GroupRoleMembersQuery : GroupManager.GroupRoleMembersQuery {
    private UUID groupID
    private UUID requestID
    private UUID roleID

    AutoValue_GroupManager_GroupRoleMembersQuery(UUID uuid, UUID uuid2, UUID uuid3) {
        if (uuid == null) {
            throw NullPointerException("Null groupID")
        }
        this.groupID = uuid
        if (uuid2 == null) {
            throw NullPointerException("Null roleID")
        }
        this.roleID = uuid2
        if (uuid3 == null) {
            throw NullPointerException("Null requestID")
        }
        this.requestID = uuid3
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is GroupManager.GroupRoleMembersQuery)) {
            return false
        }
        GroupManager.GroupRoleMembersQuery groupRoleMembersQuery = (GroupManager.GroupRoleMembersQuery) obj
        if (!this.groupID.equals(groupRoleMembersQuery.groupID()) || !this.roleID.equals(groupRoleMembersQuery.roleID())) {
            return false
        }
        return this.requestID.equals(groupRoleMembersQuery.requestID())
    }

    fun groupID(): UUID {
        return this.groupID
    }

    fun hashCode(): Int {
        return ((((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.roleID.hashCode()) * 1000003) ^ this.requestID.hashCode()
    }

    fun requestID(): UUID {
        return this.requestID
    }

    fun roleID(): UUID {
        return this.roleID
    }

    fun toString(): String {
        return "GroupRoleMembersQuery{groupID=" + this.groupID + ", " + "roleID=" + this.roleID + ", " + "requestID=" + this.requestID + "}"
    }
}
