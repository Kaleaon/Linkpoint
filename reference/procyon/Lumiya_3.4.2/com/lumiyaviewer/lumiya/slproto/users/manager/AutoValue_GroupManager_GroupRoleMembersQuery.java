// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

final class AutoValue_GroupManager_GroupRoleMembersQuery extends GroupRoleMembersQuery
{
    private final UUID groupID;
    private final UUID requestID;
    private final UUID roleID;
    
    AutoValue_GroupManager_GroupRoleMembersQuery(final UUID groupID, final UUID roleID, final UUID requestID) {
        if (groupID == null) {
            throw new NullPointerException("Null groupID");
        }
        this.groupID = groupID;
        if (roleID == null) {
            throw new NullPointerException("Null roleID");
        }
        this.roleID = roleID;
        if (requestID == null) {
            throw new NullPointerException("Null requestID");
        }
        this.requestID = requestID;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o == this) {
            return true;
        }
        if (o instanceof GroupRoleMembersQuery) {
            final GroupRoleMembersQuery groupRoleMembersQuery = (GroupRoleMembersQuery)o;
            boolean equals = b;
            if (this.groupID.equals(groupRoleMembersQuery.groupID())) {
                equals = b;
                if (this.roleID.equals(groupRoleMembersQuery.roleID())) {
                    equals = this.requestID.equals(groupRoleMembersQuery.requestID());
                }
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public UUID groupID() {
        return this.groupID;
    }
    
    @Override
    public int hashCode() {
        return ((this.groupID.hashCode() ^ 0xF4243) * 1000003 ^ this.roleID.hashCode()) * 1000003 ^ this.requestID.hashCode();
    }
    
    @Override
    public UUID requestID() {
        return this.requestID;
    }
    
    @Override
    public UUID roleID() {
        return this.roleID;
    }
    
    @Override
    public String toString() {
        return "GroupRoleMembersQuery{groupID=" + this.groupID + ", " + "roleID=" + this.roleID + ", " + "requestID=" + this.requestID + "}";
    }
}
