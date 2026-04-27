package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdAcceptTeleport extends RLVCmdGenericRestriction {
    public RLVCmdAcceptTeleport() {
        super(RLVRestrictionType.accepttp, true);
    }
}
