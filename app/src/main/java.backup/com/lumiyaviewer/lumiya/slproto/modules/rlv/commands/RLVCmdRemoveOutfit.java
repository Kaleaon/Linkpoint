package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

public class RLVCmdRemoveOutfit extends RLVCmdGenericRestriction {
    public RLVCmdRemoveOutfit() {
        super(RLVRestrictionType.remoutfit, true);
    }

    /* access modifiers changed from: protected */
    public void HandleForce(RLVController rLVController, UUID uuid, String str) {
        SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance;
        for (SLWearableType sLWearableType : SLWearableType.values()) {
            if (!sLWearableType.isBodyPart()) {
                String name = sLWearableType.getName();
                if (str.equals("") || name.equalsIgnoreCase(str)) {
                    sLAvatarAppearance.ForceTakeItemOff(sLWearableType);
                }
            }
        }
    }
}
