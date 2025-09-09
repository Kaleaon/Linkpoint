package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdAcceptTeleport extends RLVCmdGenericRestriction {
    public RLVCmdAcceptTeleport() {
        super(RLVRestrictionType.accepttp, true);
    }
}
