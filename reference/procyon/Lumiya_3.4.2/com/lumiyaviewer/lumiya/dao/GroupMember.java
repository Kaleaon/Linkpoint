// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupMember
{
    private long agentPowers;
    private int contribution;
    private UUID groupID;
    private boolean isOwner;
    private String onlineStatus;
    private UUID requestID;
    private String title;
    private UUID userID;
    
    public GroupMember() {
    }
    
    public GroupMember(final UUID groupID, final UUID requestID, final UUID userID, final int contribution, final String onlineStatus, final long agentPowers, final String title, final boolean isOwner) {
        this.groupID = groupID;
        this.requestID = requestID;
        this.userID = userID;
        this.contribution = contribution;
        this.onlineStatus = onlineStatus;
        this.agentPowers = agentPowers;
        this.title = title;
        this.isOwner = isOwner;
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
    
    public void setAgentPowers(final long agentPowers) {
        this.agentPowers = agentPowers;
    }
    
    public void setContribution(final int contribution) {
        this.contribution = contribution;
    }
    
    public void setGroupID(final UUID groupID) {
        this.groupID = groupID;
    }
    
    public void setIsOwner(final boolean isOwner) {
        this.isOwner = isOwner;
    }
    
    public void setOnlineStatus(final String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
    
    public void setRequestID(final UUID requestID) {
        this.requestID = requestID;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public void setUserID(final UUID userID) {
        this.userID = userID;
    }
}
