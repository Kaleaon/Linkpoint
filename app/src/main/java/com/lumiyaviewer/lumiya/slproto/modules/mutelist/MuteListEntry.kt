package com.lumiyaviewer.lumiya.slproto.modules.mutelist

import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID
import javax.annotation.concurrent.Immutable

@Immutable
data class MuteListEntry(
    val type: MuteType,
    val uuid: UUID,
    val name: String,
    val flags: Int,
) {
    constructor(muteType: MuteType, uuid2: UUID, str: String, i: Int) : this(
        muteType,
        UUIDPool.getUUID(uuid2),
        str,
        i,
    )

    companion object {
        const val flagAll = 15
        const val flagObjectSounds = 8
        const val flagParticles = 4
        const val flagTextChat = 1
        const val flagVoiceChat = 2
    }
}