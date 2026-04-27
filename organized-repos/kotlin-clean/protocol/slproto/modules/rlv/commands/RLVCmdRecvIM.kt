package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdRecvIM : RLVCmdGenericRestriction() {
    public RLVCmdRecvIM() {
        super(RLVRestrictionType.recvim, true)
    }
}
