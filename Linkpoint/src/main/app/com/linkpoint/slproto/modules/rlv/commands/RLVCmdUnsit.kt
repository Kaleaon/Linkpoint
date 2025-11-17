package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdUnsit : RLVCmdGenericRestriction {
    RLVCmdUnsit() {
        super(RLVRestrictionType.unsit, false)
    }

    /* access modifiers changed from: protected */
    Unit HandleForce(RLVController rLVController, UUID uuid, String str) {
        rLVController.getModules().avatarControl.ForceStand()
    }
}
