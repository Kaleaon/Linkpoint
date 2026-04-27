package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdRecvChat extends RLVCmdGenericRestriction {
    public RLVCmdRecvChat() {
        super(RLVRestrictionType.recvchat, true);
    }
}
