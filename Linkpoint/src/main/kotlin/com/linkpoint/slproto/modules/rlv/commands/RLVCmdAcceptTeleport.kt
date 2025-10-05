package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdAcceptTeleport : RLVCmdGenericRestriction() {
    public RLVCmdAcceptTeleport() {
        super(RLVRestrictionType.accepttp, true)
    }
}
