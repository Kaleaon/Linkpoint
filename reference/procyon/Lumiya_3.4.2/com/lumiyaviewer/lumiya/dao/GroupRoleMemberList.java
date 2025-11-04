// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupRoleMemberList
{
    private UUID groupID;
    private boolean mustRevalidate;
    private UUID requestID;
    
    public GroupRoleMemberList() {
    }
    
    public GroupRoleMemberList(final UUID groupID) {
        this.groupID = groupID;
    }
    
    public GroupRoleMemberList(final UUID groupID, final UUID requestID, final boolean mustRevalidate) {
        this.groupID = groupID;
        this.requestID = requestID;
        this.mustRevalidate = mustRevalidate;
    }
    
    public UUID getGroupID() {
        return this.groupID;
    }
    
    public boolean getMustRevalidate() {
        return this.mustRevalidate;
    }
    
    public UUID getRequestID() {
        return this.requestID;
    }
    
    public void setGroupID(final UUID groupID) {
        this.groupID = groupID;
    }
    
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }
    
    public void setRequestID(final UUID requestID) {
        this.requestID = requestID;
    }
}
