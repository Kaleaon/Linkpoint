// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupMemberList
{
    private UUID groupID;
    private UUID requestID;
    
    public GroupMemberList() {
    }
    
    public GroupMemberList(final UUID groupID) {
        this.groupID = groupID;
    }
    
    public GroupMemberList(final UUID groupID, final UUID requestID) {
        this.groupID = groupID;
        this.requestID = requestID;
    }
    
    public UUID getGroupID() {
        return this.groupID;
    }
    
    public UUID getRequestID() {
        return this.requestID;
    }
    
    public void setGroupID(final UUID groupID) {
        this.groupID = groupID;
    }
    
    public void setRequestID(final UUID requestID) {
        this.requestID = requestID;
    }
}
