package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdRezObjects extends RLVCmdGenericRestriction {
    public RLVCmdRezObjects() {
        super(RLVRestrictionType.rez, false);
    }
}
