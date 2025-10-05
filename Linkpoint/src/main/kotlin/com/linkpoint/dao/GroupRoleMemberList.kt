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

    public UUID getGroupID() {
        return this.groupID
    }

    public Boolean getMustRevalidate() {
        return this.mustRevalidate
    }

    public UUID getRequestID() {
        return this.requestID
    }

    fun setGroupID(UUID uuid) {
        this.groupID = uuid
    }

    fun setMustRevalidate(Boolean z) {
        this.mustRevalidate = z
    }

    fun setRequestID(UUID uuid) {
        this.requestID = uuid
    }
}
