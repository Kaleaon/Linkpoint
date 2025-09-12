package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdViewNotecard extends RLVCmdGenericRestriction {
    public RLVCmdViewNotecard() {
        super(RLVRestrictionType.viewnote, false);
    }
}
