package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

public class RLVCmdDetach extends RLVCmdGenericRestriction {
    public RLVCmdDetach() {
        super(RLVRestrictionType.detach, true);
    }

    /* access modifiers changed from: protected */
    public void HandleForce(RLVController rLVController, UUID uuid, String str) {
        UUID attachmentUUID;
        SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance;
        for (int i = 0; i < 56; i++) {
            if (SLAttachmentPoint.attachmentPoints[i] != null) {
                String lowerCase = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase();
                if ((str.equals("") || lowerCase.equalsIgnoreCase(str)) && (attachmentUUID = sLAvatarAppearance.getAttachmentUUID(i)) != null && rLVController.getRestrictions().isAllowed(RLVRestrictionType.detach, lowerCase, attachmentUUID)) {
                    sLAvatarAppearance.DetachItemFromPoint(i);
                }
            }
        }
    }
}
