package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSendChannel extends RLVCmdGenericRestriction {
    public RLVCmdSendChannel() {
        super(RLVRestrictionType.sendchannel, true);
    }
}
