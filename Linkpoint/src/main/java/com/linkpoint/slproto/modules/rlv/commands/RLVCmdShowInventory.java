package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdShowInventory extends RLVCmdGenericRestriction {
    public RLVCmdShowInventory() {
        super(RLVRestrictionType.showinv, false);
    }
}
