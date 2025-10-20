package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdAddOutfit : RLVCmdGenericRestriction() {
    public RLVCmdAddOutfit() {
        super(RLVRestrictionType.addoutfit, true)
    }
}
