package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdTeleportLocation extends RLVCmdGenericRestriction {
    public RLVCmdTeleportLocation() {
        super(RLVRestrictionType.tploc, false);
    }
}
