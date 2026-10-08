package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import java.util.UUID;
/* loaded from: classes.dex */
final class AutoValue_GroupManager_GroupMemberRolesQuery extends GroupManager.GroupMemberRolesQuery {
    private final UUID groupID;
    private final UUID memberID;
    private final UUID requestID;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_GroupManager_GroupMemberRolesQuery(UUID uuid, UUID uuid2, UUID uuid3) {
        if (uuid == null) {
            throw new NullPointerException("Null groupID");
        }
        this.groupID = uuid;
        if (uuid2 == null) {
            throw new NullPointerException("Null memberID");
        }
        this.memberID = uuid2;
        if (uuid3 == null) {
            throw new NullPointerException("Null requestID");
        }
        this.requestID = uuid3;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GroupManager.GroupMemberRolesQuery) {
            GroupManager.GroupMemberRolesQuery groupMemberRolesQuery = (GroupManager.GroupMemberRolesQuery) obj;
            if (this.groupID.equals(groupMemberRolesQuery.groupID()) && this.memberID.equals(groupMemberRolesQuery.memberID())) {
                return this.requestID.equals(groupMemberRolesQuery.requestID());
            }
            return false;
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager.GroupMemberRolesQuery
    public UUID groupID() {
        return this.groupID;
    }

    public int hashCode() {
        return ((((this.groupID.hashCode() ^ 1000003) * 1000003) ^ this.memberID.hashCode()) * 1000003) ^ this.requestID.hashCode();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager.GroupMemberRolesQuery
    public UUID memberID() {
        return this.memberID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager.GroupMemberRolesQuery
    public UUID requestID() {
        return this.requestID;
    }

    public String toString() {
        return "GroupMemberRolesQuery{groupID=" + this.groupID + ", memberID=" + this.memberID + ", requestID=" + this.requestID + "}";
    }
}
