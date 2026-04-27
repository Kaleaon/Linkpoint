package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdTeleportLandmark extends RLVCmdGenericRestriction {
    public RLVCmdTeleportLandmark() {
        super(RLVRestrictionType.tplm, false);
    }
}
