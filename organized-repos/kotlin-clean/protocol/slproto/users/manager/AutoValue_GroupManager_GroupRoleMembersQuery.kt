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

    public Boolean equals(Object obj) {
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

    public UUID groupID() {
        return this.groupID
    }

    public Int hashCode() {
        return ((((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.roleID.hashCode()) * 1000003) ^ this.requestID.hashCode()
    }

    public UUID requestID() {
        return this.requestID
    }

    public UUID roleID() {
        return this.roleID
    }

    public String toString() {
        return "GroupRoleMembersQuery{groupID=" + this.groupID + ", " + "roleID=" + this.roleID + ", " + "requestID=" + this.requestID + "}"
    }
}
