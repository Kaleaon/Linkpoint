package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

/* loaded from: classes.dex */
public class RLVCmdSit extends RLVCmdGenericRestriction {
    public RLVCmdSit() {
        super(RLVRestrictionType.sit, false);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.rlv.commands.RLVCmdGenericRestriction
    protected void HandleForce(RLVController rLVController, UUID uuid, String str) {
        if (str != null) {
            try {
                rLVController.getModules().avatarControl.ForceSitOnObject(UUID.fromString(str));
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }
}
