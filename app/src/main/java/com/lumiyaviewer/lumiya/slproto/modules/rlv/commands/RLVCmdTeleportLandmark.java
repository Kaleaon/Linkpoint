package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdTeleportLandmark extends RLVCmdGenericRestriction {
    public RLVCmdTeleportLandmark() {
        super(RLVRestrictionType.tplm, false);
    }
}
