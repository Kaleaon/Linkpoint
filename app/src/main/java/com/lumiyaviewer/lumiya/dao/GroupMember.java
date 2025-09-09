package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupMember {
    private long agentPowers;
    private int contribution;
    private UUID groupID;
    private boolean isOwner;
    private String onlineStatus;
    private UUID requestID;
    private String title;
    private UUID userID;

    public GroupMember(UUID uuid, UUID uuid2, UUID uuid3, int i, String str, long j, String str2, boolean z) {
        this.groupID = uuid;
        this.requestID = uuid2;
        this.userID = uuid3;
        this.contribution = i;
        this.onlineStatus = str;
        this.agentPowers = j;
        this.title = str2;
        this.isOwner = z;
    }

    public long getAgentPowers() {
        return this.agentPowers;
    }

    public int getContribution() {
        return this.contribution;
    }

    public UUID getGroupID() {
        return this.groupID;
    }

    public boolean getIsOwner() {
        return this.isOwner;
    }

    public String getOnlineStatus() {
        return this.onlineStatus;
    }

    public UUID getRequestID() {
        return this.requestID;
    }

    public String getTitle() {
        return this.title;
    }

    public UUID getUserID() {
        return this.userID;
    }

    public void setAgentPowers(long j) {
        this.agentPowers = j;
    }

    public void setContribution(int i) {
        this.contribution = i;
    }

    public void setGroupID(UUID uuid) {
        this.groupID = uuid;
    }

    public void setIsOwner(boolean z) {
        this.isOwner = z;
    }

    public void setOnlineStatus(String str) {
        this.onlineStatus = str;
    }

    public void setRequestID(UUID uuid) {
        this.requestID = uuid;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public void setUserID(UUID uuid) {
        this.userID = uuid;
    }
}
