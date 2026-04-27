// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupRoleMember
{
    private UUID groupID;
    private UUID requestID;
    private UUID roleID;
    private UUID userID;
    
    public GroupRoleMember() {
    }
    
    public GroupRoleMember(final UUID groupID, final UUID requestID, final UUID roleID, final UUID userID) {
        this.groupID = groupID;
        this.requestID = requestID;
        this.roleID = roleID;
        this.userID = userID;
    }
    
    public UUID getGroupID() {
        return this.groupID;
    }
    
    public UUID getRequestID() {
        return this.requestID;
    }
    
    public UUID getRoleID() {
        return this.roleID;
    }
    
    public UUID getUserID() {
        return this.userID;
    }
    
    public void setGroupID(final UUID groupID) {
        this.groupID = groupID;
    }
    
    public void setRequestID(final UUID requestID) {
        this.requestID = requestID;
    }
    
    public void setRoleID(final UUID roleID) {
        this.roleID = roleID;
    }
    
    public void setUserID(final UUID userID) {
        this.userID = userID;
    }
}
