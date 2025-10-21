package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdTeleportLocation : RLVCmdGenericRestriction() {
    public RLVCmdTeleportLocation() {
        super(RLVRestrictionType.tploc, false)
    }
}
