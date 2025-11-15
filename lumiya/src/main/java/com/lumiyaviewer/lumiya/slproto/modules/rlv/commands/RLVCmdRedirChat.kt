package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType
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
