package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSendChannel extends RLVCmdGenericRestriction {
    public RLVCmdSendChannel() {
        super(RLVRestrictionType.sendchannel, true);
    }
}
