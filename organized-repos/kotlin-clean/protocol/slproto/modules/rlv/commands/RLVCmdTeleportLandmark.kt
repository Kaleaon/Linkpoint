package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdTeleportLandmark : RLVCmdGenericRestriction() {
    public RLVCmdTeleportLandmark() {
        super(RLVRestrictionType.tplm, false)
    }
}
