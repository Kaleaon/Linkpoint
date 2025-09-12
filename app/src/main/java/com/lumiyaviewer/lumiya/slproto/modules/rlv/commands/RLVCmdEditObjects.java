package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdEditObjects extends RLVCmdGenericRestriction {
    public RLVCmdEditObjects() {
        super(RLVRestrictionType.edit, true);
    }
}
