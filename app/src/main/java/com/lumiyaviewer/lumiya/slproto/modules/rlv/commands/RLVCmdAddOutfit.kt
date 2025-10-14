package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType

class RLVCmdAddOutfit : RLVCmdGenericRestriction {
    RLVCmdAddOutfit() {
        super(RLVRestrictionType.addoutfit, true)
    }
}
