package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdShowInventory : RLVCmdGenericRestriction() {
    public RLVCmdShowInventory() {
        super(RLVRestrictionType.showinv, false)
    }
}
