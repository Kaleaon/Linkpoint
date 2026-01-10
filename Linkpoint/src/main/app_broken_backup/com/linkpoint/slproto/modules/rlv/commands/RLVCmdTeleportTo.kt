package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.types.LLVector3
import java.util.UUID

class RLVCmdTeleportTo : RLVCommand {
    fun Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2)  {
        if (str.equals("force") && str2 != null) {
            Array<String> split = str2.split("/")
            if (split.size >= 3) {
                try {
                    rLVController.teleportToGlobalPos(uuid, LLVector3(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2])))
                } catch (NumberFormatException e) {
                    Debug.Warning(e)
                }
            }
        }
    }
}
