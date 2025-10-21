package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdSendIM : RLVCmdGenericRestriction() {
    public RLVCmdSendIM() {
        super(RLVRestrictionType.sendim, true)
    }
}
