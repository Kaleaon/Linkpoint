package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdTeleportLure : RLVCmdGenericRestriction() {
    public RLVCmdTeleportLure() {
        super(RLVRestrictionType.tplure, true)
    }
}
