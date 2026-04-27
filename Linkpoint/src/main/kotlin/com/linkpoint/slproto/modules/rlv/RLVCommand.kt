package com.linkpoint.slproto.modules.rlv

import java.util.UUID

interface RLVCommand {
    fun Handle(rLVController: RLVController, uuid: UUID, rLVCommands: RLVCommands, str: String, str2: String)
}
