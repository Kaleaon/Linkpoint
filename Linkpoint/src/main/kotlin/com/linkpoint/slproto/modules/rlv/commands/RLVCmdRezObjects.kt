package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdRezObjects : RLVCmdGenericRestriction() {
    public RLVCmdRezObjects() {
        super(RLVRestrictionType.rez, false)
    }
}
