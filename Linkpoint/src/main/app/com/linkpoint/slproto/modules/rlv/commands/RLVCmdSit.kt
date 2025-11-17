package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdSit : RLVCmdGenericRestriction {
    RLVCmdSit() {
        super(RLVRestrictionType.sit, false)
    }

    /* access modifiers changed from: protected */
    Unit HandleForce(RLVController rLVController, UUID uuid, String str) {
        if (str != null) {
            try {
                rLVController.getModules().avatarControl.ForceSitOnObject(UUID.fromString(str))
            } catch (Exception e) {
                Debug.Warning(e)
            }
        }
    }
}
