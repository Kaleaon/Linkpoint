// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import java.util.UUID;

public class MuteListKey
{
    public final MuteType muteType;
    public final UUID uuid;
    
    public MuteListKey(final MuteListEntry muteListEntry) {
        this.muteType = muteListEntry.type;
        this.uuid = muteListEntry.uuid;
    }
    
    public MuteListKey(final MuteType muteType, final UUID uuid) {
        this.muteType = muteType;
        this.uuid = uuid;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MuteListKey)) {
            return false;
        }
        final MuteListKey muteListKey = (MuteListKey)o;
        if (this.muteType != muteListKey.muteType) {
            return false;
        }
        boolean b;
        if (this.uuid == null) {
            b = true;
        }
        else {
            b = false;
        }
        boolean b2;
        if (muteListKey.uuid == null) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        return b == b2 && (this.uuid == null || this.uuid.equals(muteListKey.uuid));
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.muteType != null) {
            n = this.muteType.hashCode() + 0;
        }
        int n2 = n;
        if (this.uuid != null) {
            n2 = n + this.uuid.hashCode();
        }
        return n2;
    }
}
