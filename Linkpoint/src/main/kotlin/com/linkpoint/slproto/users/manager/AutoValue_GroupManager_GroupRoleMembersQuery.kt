package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.manager.GroupManager
import java.util.UUID

final class AutoValue_GroupManager_GroupRoleMembersQuery : GroupManager().GroupRoleMembersQuery {
    private val UUID groupID
    private val UUID requestID
    private val UUID roleID

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

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof GroupManager.GroupRoleMembersQuery)) {
            return false
        }
        GroupManager.GroupRoleMembersQuery groupRoleMembersQuery = (GroupManager.GroupRoleMembersQuery) obj
        if (!this.groupID.equals(groupRoleMembersQuery.groupID()) || !this.roleID.equals(groupRoleMembersQuery.roleID())) {
            return false
        }
        return this.requestID.equals(groupRoleMembersQuery.requestID())
    }

     public fun groupID(): UUID {
        return this.groupID
    }

     public override fun hashCode(): Int {
        return ((((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.roleID.hashCode()) * 1000003) ^ this.requestID.hashCode()
    }

     public fun requestID(): UUID {
        return this.requestID
    }

     public fun roleID(): UUID {
        return this.roleID
    }

     public override fun toString(): String {
        return "GroupRoleMembersQuery{groupID=" + this.groupID + ", " + "roleID=" + this.roleID + ", " + "requestID=" + this.requestID + "}"
    }
}
