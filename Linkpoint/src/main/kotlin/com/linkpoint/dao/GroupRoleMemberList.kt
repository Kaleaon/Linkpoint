package com.linkpoint.dao

import java.util.UUID

class GroupRoleMemberList {
    private UUID groupID
    private Boolean mustRevalidate
    private UUID requestID

    public GroupRoleMemberList(UUID uuid) {
        this.groupID = uuid
    }

    public GroupRoleMemberList(UUID uuid, UUID uuid2, Boolean z) {
        this.groupID = uuid
        this.requestID = uuid2
        this.mustRevalidate = z
    }

     public fun getGroupID(): UUID {
        return this.groupID
    }

     public fun getMustRevalidate(): Boolean {
        return this.mustRevalidate
    }

     public fun getRequestID(): UUID {
        return this.requestID
    }

    fun setGroupID(uuid: UUID) {
        this.groupID = uuid
    }

    fun setMustRevalidate(z: Boolean) {
        this.mustRevalidate = z
    }

    fun setRequestID(uuid: UUID) {
        this.requestID = uuid
    }
}
