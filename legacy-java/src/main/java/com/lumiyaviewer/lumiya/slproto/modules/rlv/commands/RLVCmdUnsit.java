package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

/* loaded from: classes.dex */
public class RLVCmdUnsit extends RLVCmdGenericRestriction {
    public RLVCmdUnsit() {
        super(RLVRestrictionType.unsit, false);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGenericRestriction
    protected void HandleForce(RLVController rLVController, UUID uuid, String str) {
        rLVController.getModules().avatarControl.ForceStand();
    }
}
