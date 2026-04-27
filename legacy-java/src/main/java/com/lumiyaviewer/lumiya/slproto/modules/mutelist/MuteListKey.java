package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import java.util.UUID;

/* loaded from: classes.dex */
public class MuteListKey {
    public final MuteType muteType;
    public final UUID uuid;

    public MuteListKey(MuteListEntry muteListEntry) {
        this.muteType = muteListEntry.type;
        this.uuid = muteListEntry.uuid;
    }

    public MuteListKey(MuteType muteType, UUID uuid) {
        this.muteType = muteType;
        this.uuid = uuid;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MuteListKey)) {
            return false;
        }
        MuteListKey muteListKey = (MuteListKey) obj;
        if (this.muteType != muteListKey.muteType) {
            return false;
        }
        if ((this.uuid == null) != (muteListKey.uuid == null)) {
            return false;
        }
        return this.uuid == null || this.uuid.equals(muteListKey.uuid);
    }

    public int hashCode() {
        int hashCode = this.muteType != null ? this.muteType.hashCode() + 0 : 0;
        return this.uuid != null ? hashCode + this.uuid.hashCode() : hashCode;
    }
}
