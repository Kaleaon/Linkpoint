package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdTeleportSit extends RLVCmdGenericRestriction {
    public RLVCmdTeleportSit() {
        super(RLVRestrictionType.sittp, false);
    }
}
