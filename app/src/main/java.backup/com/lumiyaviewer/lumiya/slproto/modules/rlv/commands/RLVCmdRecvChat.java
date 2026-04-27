package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdRecvChat extends RLVCmdGenericRestriction {
    public RLVCmdRecvChat() {
        super(RLVRestrictionType.recvchat, true);
    }
}
