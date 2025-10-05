package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdTeleportLocation extends RLVCmdGenericRestriction {
    public RLVCmdTeleportLocation() {
        super(RLVRestrictionType.tploc, false);
    }
}
