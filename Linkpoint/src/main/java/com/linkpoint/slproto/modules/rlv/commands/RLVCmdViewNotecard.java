package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdViewNotecard extends RLVCmdGenericRestriction {
    public RLVCmdViewNotecard() {
        super(RLVRestrictionType.viewnote, false);
    }
}
