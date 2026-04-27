package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdAddOutfit extends RLVCmdGenericRestriction {
    public RLVCmdAddOutfit() {
        super(RLVRestrictionType.addoutfit, true);
    }
}
