package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdRedirChat : RLVCommand {
    override fun Handle(
        controller: RLVController,
        objectUUID: UUID,
        commands: RLVCommands,
        param1: String,
        param2: String,
    ) {
        val channel = param2.ifEmpty { "" }

        if (channel.isNotEmpty()) {
            try {
                val channelNum = channel.toInt()
                when (param1) {
                    "n", "add" -> {
                        controller.restrictions.addRestriction(
                            RLVRestrictionType.redirchat,
                            objectUUID,
                            channelNum.toString(),
                        )
                    }
                    "y", "rem" -> {
                        controller.restrictions.removeRestriction(
                            RLVRestrictionType.redirchat,
                            objectUUID,
                            channelNum.toString(),
                        )
                    }
                }
            } catch (e: NumberFormatException) {
                Debug.Warning(e)
            }
        }
    }
}
