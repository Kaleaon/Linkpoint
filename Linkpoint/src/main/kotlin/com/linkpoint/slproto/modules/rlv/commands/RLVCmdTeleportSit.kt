package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdTeleportSit : RLVCmdGenericRestriction() {
    public RLVCmdTeleportSit() {
        super(RLVRestrictionType.sittp, false)
    }
}
