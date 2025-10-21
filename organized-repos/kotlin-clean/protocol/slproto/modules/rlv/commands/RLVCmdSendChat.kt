package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdSendChat : RLVCmdGenericRestriction() {
    public RLVCmdSendChat() {
        super(RLVRestrictionType.sendchat, false)
    }
}
