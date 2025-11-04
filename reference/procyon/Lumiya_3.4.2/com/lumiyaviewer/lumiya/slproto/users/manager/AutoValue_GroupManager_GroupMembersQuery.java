// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

final class AutoValue_GroupManager_GroupMembersQuery extends GroupMembersQuery
{
    private final UUID groupID;
    private final UUID requestID;
    
    AutoValue_GroupManager_GroupMembersQuery(final UUID groupID, final UUID requestID) {
        if (groupID == null) {
            throw new NullPointerException("Null groupID");
        }
        this.groupID = groupID;
        if (requestID == null) {
            throw new NullPointerException("Null requestID");
        }
        this.requestID = requestID;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (o == this) {
            return true;
        }
        if (o instanceof GroupMembersQuery) {
            final GroupMembersQuery groupMembersQuery = (GroupMembersQuery)o;
            if (this.groupID.equals(groupMembersQuery.groupID())) {
                equals = this.requestID.equals(groupMembersQuery.requestID());
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
        return (this.groupID.hashCode() ^ 0xF4243) * 1000003 ^ this.requestID.hashCode();
    }
    
    @Override
    public UUID requestID() {
        return this.requestID;
    }
    
    @Override
    public String toString() {
        return "GroupMembersQuery{groupID=" + this.groupID + ", " + "requestID=" + this.requestID + "}";
    }
}
