package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdEditObjects : RLVCmdGenericRestriction() {
    public RLVCmdEditObjects() {
        super(RLVRestrictionType.edit, true)
    }
}
