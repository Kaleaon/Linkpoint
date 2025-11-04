// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

import java.util.UUID;

public class SLJoinLeaveGroupEvent
{
    public final UUID groupID;
    public final boolean isJoin;
    public final boolean success;
    
    public SLJoinLeaveGroupEvent(final UUID groupID, final boolean isJoin, final boolean success) {
        this.groupID = groupID;
        this.isJoin = isJoin;
        this.success = success;
    }
}
