package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager
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

    Boolean equals(Any obj) {
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

    UUID groupID() {
        return this.groupID
    }

    Int hashCode() {
        return ((((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.roleID.hashCode()) * 1000003) ^ this.requestID.hashCode()
    }

    UUID requestID() {
        return this.requestID
    }

    UUID roleID() {
        return this.roleID
    }

    String toString() {
        return "GroupRoleMembersQuery{groupID=" + this.groupID + ", " + "roleID=" + this.roleID + ", " + "requestID=" + this.requestID + "}"
    }
}
