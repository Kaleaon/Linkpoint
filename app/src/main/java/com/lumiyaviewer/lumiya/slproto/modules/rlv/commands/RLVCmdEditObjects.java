package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdEditObjects extends RLVCmdGenericRestriction {
    public RLVCmdEditObjects() {
        super(RLVRestrictionType.edit, true);
    }
}
