package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType

class RLVCmdViewNotecard : RLVCmdGenericRestriction() {
    public RLVCmdViewNotecard() {
        super(RLVRestrictionType.viewnote, false)
    }
}
