// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.events;

import java.util.UUID;

public class EventUserInfoChanged
{
    public static final int CHANGED_NAME = 2;
    public static final int CHANGED_PROFILE = 4;
    public final UUID agentUUID;
    public final int changedMask;
    public final UUID userUUID;
    
    public EventUserInfoChanged(final UUID agentUUID, final UUID userUUID, final int changedMask) {
        this.agentUUID = agentUUID;
        this.userUUID = userUUID;
        this.changedMask = changedMask;
    }
    
    public boolean isNameChanged() {
        boolean b = false;
        if ((this.changedMask & 0x2) != 0x0) {
            b = true;
        }
        return b;
    }
    
    public boolean isProfileChanged() {
        boolean b = false;
        if ((this.changedMask & 0x4) != 0x0) {
            b = true;
        }
        return b;
    }
}
