package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdRedirChat : RLVCommand {
    fun Handle(rLVController: RLVController, uuid: UUID, rLVCommands: RLVCommands, str: String, str2: String) {
        if (str2 == null) {
            str2 = ""
        }
        if (!str2.equals("")) {
            try {
                val parseInt: Int = Integer.parseInt(str2)
                if (str.equals("n") || str.equals("add")) {
                    rLVController.getRestrictions().addRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(parseInt))
                } else if (str.equals("y") || str.equals("rem")) {
                    rLVController.getRestrictions().removeRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(parseInt))
                }
            } catch (NumberFormatException e) {
                Debug.Warning(e)
            }
        }
    }
}
