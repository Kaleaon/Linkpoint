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

    public Unit setAgentPowers(Long j) {
        this.agentPowers = j
    }

    public Unit setContribution(Int i) {
        this.contribution = i
    }

    public Unit setGroupID(UUID uuid) {
        this.groupID = uuid
    }

    public Unit setIsOwner(Boolean z) {
        this.isOwner = z
    }

    public Unit setOnlineStatus(String str) {
        this.onlineStatus = str
    }

    public Unit setRequestID(UUID uuid) {
        this.requestID = uuid
    }

    public Unit setTitle(String str) {
        this.title = str
    }

    public Unit setUserID(UUID uuid) {
        this.userID = uuid
    }
}
