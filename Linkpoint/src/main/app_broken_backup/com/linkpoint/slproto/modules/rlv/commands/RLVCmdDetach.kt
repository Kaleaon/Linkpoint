package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdDetach : RLVCmdGenericRestriction {
    RLVCmdDetach() {
        super(RLVRestrictionType.detach, true)
    }

    /* access modifiers changed from: protected */
    fun HandleForce(RLVController rLVController, UUID uuid, String str)  {
        UUID attachmentUUID
        SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance
        for (i in 0 until 56) {
            if (SLAttachmentPoint.attachmentPoints[i] != null) {
                var lowerCase: String = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase()
                if ((str.equals("") || lowerCase.equalsIgnoreCase(str)) && (attachmentUUID = sLAvatarAppearance.getAttachmentUUID(i)) != null && rLVController.getRestrictions().isAllowed(RLVRestrictionType.detach, lowerCase, attachmentUUID)) {
                    sLAvatarAppearance.DetachItemFromPoint(i)
                }
            }
        }
    }
}
