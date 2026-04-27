package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdSendChannel : RLVCmdGenericRestriction() {
    public RLVCmdSendChannel() {
        super(RLVRestrictionType.sendchannel, true)
    }
}
