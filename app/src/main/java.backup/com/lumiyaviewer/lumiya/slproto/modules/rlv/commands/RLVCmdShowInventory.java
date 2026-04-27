package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdShowInventory extends RLVCmdGenericRestriction {
    public RLVCmdShowInventory() {
        super(RLVRestrictionType.showinv, false);
    }
}
