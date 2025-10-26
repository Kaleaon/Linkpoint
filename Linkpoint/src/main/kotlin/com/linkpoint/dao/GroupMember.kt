package com.linkpoint.dao

import java.util.UUID

class GroupMember {
    private Long agentPowers
    private Int contribution
    private UUID groupID
    private Boolean isOwner
    private String onlineStatus
    private UUID requestID
    private String title
    private UUID userID

    public GroupMember(UUID uuid, UUID uuid2, UUID uuid3, Int i, String str, Long j, String str2, Boolean z) {
        this.groupID = uuid
        this.requestID = uuid2
        this.userID = uuid3
        this.contribution = i
        this.onlineStatus = str
        this.agentPowers = j
        this.title = str2
        this.isOwner = z
    }

     public fun getAgentPowers(): Long {
        return this.agentPowers
    }

     public fun getContribution(): Int {
        return this.contribution
    }

     public fun getGroupID(): UUID {
        return this.groupID
    }

     public fun getIsOwner(): Boolean {
        return this.isOwner
    }

     public fun getOnlineStatus(): String {
        return this.onlineStatus
    }

     public fun getRequestID(): UUID {
        return this.requestID
    }

     public fun getTitle(): String {
        return this.title
    }

     public fun getUserID(): UUID {
        return this.userID
    }

    fun setAgentPowers(j: Long) {
        this.agentPowers = j
    }

    fun setContribution(i: Int) {
        this.contribution = i
    }

    fun setGroupID(uuid: UUID) {
        this.groupID = uuid
    }

    fun setIsOwner(z: Boolean) {
        this.isOwner = z
    }

    fun setOnlineStatus(str: String) {
        this.onlineStatus = str
    }

    fun setRequestID(uuid: UUID) {
        this.requestID = uuid
    }

    fun setTitle(str: String) {
        this.title = str
    }

    fun setUserID(uuid: UUID) {
        this.userID = uuid
    }
}
