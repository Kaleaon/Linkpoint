package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVController;
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

public class RLVCmdUnsit extends RLVCmdGenericRestriction {
    public RLVCmdUnsit() {
        super(RLVRestrictionType.unsit, false);
    }

    /* access modifiers changed from: protected */
    public void HandleForce(RLVController rLVController, UUID uuid, String str) {
        rLVController.getModules().avatarControl.ForceStand();
    }
}
