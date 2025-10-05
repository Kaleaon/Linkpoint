package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdRecvChat : RLVCmdGenericRestriction() {
    public RLVCmdRecvChat() {
        super(RLVRestrictionType.recvchat, true)
    }
}
