package com.linkpoint.slproto.modules.mutelist

import java.util.UUID

class MuteListKey {
    MuteType muteType
    UUID uuid

    MuteListKey(MuteListEntry muteListEntry) {
        this.muteType = muteListEntry.type
        this.uuid = muteListEntry.uuid
    }

    MuteListKey(MuteType muteType2, UUID uuid2) {
        this.muteType = muteType2
        this.uuid = uuid2
    }

    Boolean equals(Any obj) {
        if (!(obj instanceof MuteListKey)) {
            return false
        }
        MuteListKey muteListKey = (MuteListKey) obj
        if (this.muteType != muteListKey.muteType) {
            return false
        }
        if ((this.uuid == null) != (muteListKey.uuid == null)) {
            return false
        }
        return this.uuid == null || this.uuid.equals(muteListKey.uuid)
    }

    Int hashCode() {
        Int i = 0
        if (this.muteType != null) {
            i = this.muteType.hashCode() + 0
        }
        return this.uuid != null ? i + this.uuid.hashCode() : i
    }
}
