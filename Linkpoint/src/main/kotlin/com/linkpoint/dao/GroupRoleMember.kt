package com.linkpoint.dao

import java.util.UUID

class GroupRoleMember {
    private UUID groupID
    private UUID requestID
    private UUID roleID
    private UUID userID

    public GroupRoleMember(UUID uuid, UUID uuid2, UUID uuid3, UUID uuid4) {
        this.groupID = uuid
        this.requestID = uuid2
        this.roleID = uuid3
        this.userID = uuid4
    }

     public fun getGroupID(): UUID {
        return this.groupID
    }

     public fun getRequestID(): UUID {
        return this.requestID
    }

     public fun getRoleID(): UUID {
        return this.roleID
    }

     public fun getUserID(): UUID {
        return this.userID
    }

    fun setGroupID(uuid: UUID) {
        this.groupID = uuid
    }

    fun setRequestID(uuid: UUID) {
        this.requestID = uuid
    }

    fun setRoleID(uuid: UUID) {
        this.roleID = uuid
    }

    fun setUserID(uuid: UUID) {
        this.userID = uuid
    }
}
