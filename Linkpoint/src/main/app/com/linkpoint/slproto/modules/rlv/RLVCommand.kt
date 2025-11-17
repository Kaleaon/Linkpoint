package com.linkpoint.slproto.modules.rlv

import java.util.UUID

interface RLVCommand {
    fun Handle(
        controller: RLVController,
        objectUUID: UUID,
        commands: RLVCommands,
        param1: String,
        param2: String,
    )
}
