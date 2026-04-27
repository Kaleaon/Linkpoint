package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSendIM extends RLVCmdGenericRestriction {
    public RLVCmdSendIM() {
        super(RLVRestrictionType.sendim, true);
    }
}
