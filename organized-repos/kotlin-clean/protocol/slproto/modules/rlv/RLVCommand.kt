package com.linkpoint.slproto.modules.rlv

import java.util.UUID

interface RLVCommand {
    Unit Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2)
}
