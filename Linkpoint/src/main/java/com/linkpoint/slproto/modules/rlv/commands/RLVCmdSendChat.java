package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSendChat extends RLVCmdGenericRestriction {
    public RLVCmdSendChat() {
        super(RLVRestrictionType.sendchat, false);
    }
}
