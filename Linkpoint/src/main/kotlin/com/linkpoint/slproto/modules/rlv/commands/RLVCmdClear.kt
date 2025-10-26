package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.HashSet
import java.util.UUID

class RLVCmdClear : RLVCommand {
    fun Handle(rLVController: RLVController, uuid: UUID, rLVCommands: RLVCommands, str: String, str2: String) {
        val hashSet: HashSet = HashSet()
        for (RLVRestrictionType rLVRestrictionType : RLVRestrictionType.values()) {
            if (str == "") {
                hashSet.add(rLVRestrictionType)
            } else if (rLVRestrictionType.toString().contains(str)) {
                hashSet.add(rLVRestrictionType)
            }
        }
        rLVController.getRestrictions().removeRestrictions(uuid, hashSet)
    }
}
