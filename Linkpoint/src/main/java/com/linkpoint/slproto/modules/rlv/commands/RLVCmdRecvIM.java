package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdRecvIM extends RLVCmdGenericRestriction {
    public RLVCmdRecvIM() {
        super(RLVRestrictionType.recvim, true);
    }
}
