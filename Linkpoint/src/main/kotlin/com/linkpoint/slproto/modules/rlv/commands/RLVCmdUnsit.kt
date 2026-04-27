package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdUnsit : RLVCmdGenericRestriction() {
    public RLVCmdUnsit() {
        super(RLVRestrictionType.unsit, false)
    }

    /* access modifiers changed from: protected */
    fun HandleForce(rLVController: RLVController, uuid: UUID, str: String) {
        rLVController.getModules().avatarControl.ForceStand()
    }
}
