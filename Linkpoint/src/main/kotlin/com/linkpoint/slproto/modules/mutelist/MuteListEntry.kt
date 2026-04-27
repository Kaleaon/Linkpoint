package com.linkpoint.slproto.modules.mutelist

import com.linkpoint.utils.UUIDPool
import java.util.UUID
import javax.annotation.concurrent.Immutable

@Immutable
class MuteListEntry {
    const val Int flagAll = 15
    const val Int flagObjectSounds = 8
    const val Int flagParticles = 4
    const val Int flagTextChat = 1
    const val Int flagVoiceChat = 2
    val Int flags
    val String name
    val MuteType type
    val UUID uuid

    public MuteListEntry(MuteType muteType, UUID uuid2, String str, Int i) {
        this.type = muteType
        this.uuid = UUIDPool.getUUID(uuid2)
        this.name = str
        this.flags = i
    }
}
