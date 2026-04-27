// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdDetach extends RLVCmdGenericRestriction
{
    public RLVCmdDetach() {
        super(RLVRestrictionType.detach, true);
    }
    
    @Override
    protected void HandleForce(final RLVController rlvController, final UUID uuid, final String anotherString) {
        final SLAvatarAppearance avatarAppearance = rlvController.getModules().avatarAppearance;
        for (int i = 0; i < 56; ++i) {
            if (SLAttachmentPoint.attachmentPoints[i] != null) {
                final String lowerCase = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase();
                if (anotherString.equals("") || lowerCase.equalsIgnoreCase(anotherString)) {
                    final UUID attachmentUUID = avatarAppearance.getAttachmentUUID(i);
                    if (attachmentUUID != null && rlvController.getRestrictions().isAllowed(RLVRestrictionType.detach, lowerCase, attachmentUUID)) {
                        avatarAppearance.DetachItemFromPoint(i);
                    }
                }
            }
        }
    }
}
