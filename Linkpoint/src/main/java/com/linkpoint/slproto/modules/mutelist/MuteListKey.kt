package com.linkpoint.slproto.modules.mutelist

import java.util.UUID

data class MuteListKey(
    val muteType: MuteType,
    val uuid: UUID?
) {
    constructor(entry: MuteListEntry) : this(entry.type, entry.uuid)
}