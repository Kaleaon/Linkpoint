package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSendChat extends RLVCmdGenericRestriction {
    public RLVCmdSendChat() {
        super(RLVRestrictionType.sendchat, false);
    }
}
