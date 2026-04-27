// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

final class AutoValue_GroupManager_GroupMemberRolesQuery extends GroupMemberRolesQuery
{
    private final UUID groupID;
    private final UUID memberID;
    private final UUID requestID;
    
    AutoValue_GroupManager_GroupMemberRolesQuery(final UUID groupID, final UUID memberID, final UUID requestID) {
        if (groupID == null) {
            throw new NullPointerException("Null groupID");
        }
        this.groupID = groupID;
        if (memberID == null) {
            throw new NullPointerException("Null memberID");
        }
        this.memberID = memberID;
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
        if (o instanceof GroupMemberRolesQuery) {
            final GroupMemberRolesQuery groupMemberRolesQuery = (GroupMemberRolesQuery)o;
            boolean equals = b;
            if (this.groupID.equals(groupMemberRolesQuery.groupID())) {
                equals = b;
                if (this.memberID.equals(groupMemberRolesQuery.memberID())) {
                    equals = this.requestID.equals(groupMemberRolesQuery.requestID());
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
        return ((this.groupID.hashCode() ^ 0xF4243) * 1000003 ^ this.memberID.hashCode()) * 1000003 ^ this.requestID.hashCode();
    }
    
    @Override
    public UUID memberID() {
        return this.memberID;
    }
    
    @Override
    public UUID requestID() {
        return this.requestID;
    }
    
    @Override
    public String toString() {
        return "GroupMemberRolesQuery{groupID=" + this.groupID + ", " + "memberID=" + this.memberID + ", " + "requestID=" + this.requestID + "}";
    }
}
