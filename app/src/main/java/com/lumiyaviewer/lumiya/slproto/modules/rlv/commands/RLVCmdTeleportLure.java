package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdTeleportLure extends RLVCmdGenericRestriction {
    public RLVCmdTeleportLure() {
        super(RLVRestrictionType.tplure, true);
    }
}
