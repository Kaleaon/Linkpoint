package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType
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
