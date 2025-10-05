package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSendIM extends RLVCmdGenericRestriction {
    public RLVCmdSendIM() {
        super(RLVRestrictionType.sendim, true);
    }
}
