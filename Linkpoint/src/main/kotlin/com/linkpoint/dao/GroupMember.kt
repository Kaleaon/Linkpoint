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

    public Long getAgentPowers() {
        return this.agentPowers
    }

    public Int getContribution() {
        return this.contribution
    }

    public UUID getGroupID() {
        return this.groupID
    }

    public Boolean getIsOwner() {
        return this.isOwner
    }

    public String getOnlineStatus() {
        return this.onlineStatus
    }

    public UUID getRequestID() {
        return this.requestID
    }

    public String getTitle() {
        return this.title
    }

    public UUID getUserID() {
        return this.userID
    }

    fun setAgentPowers(Long j) {
        this.agentPowers = j
    }

    fun setContribution(Int i) {
        this.contribution = i
    }

    fun setGroupID(UUID uuid) {
        this.groupID = uuid
    }

    fun setIsOwner(Boolean z) {
        this.isOwner = z
    }

    fun setOnlineStatus(String str) {
        this.onlineStatus = str
    }

    fun setRequestID(UUID uuid) {
        this.requestID = uuid
    }

    fun setTitle(String str) {
        this.title = str
    }

    fun setUserID(UUID uuid) {
        this.userID = uuid
    }
}
