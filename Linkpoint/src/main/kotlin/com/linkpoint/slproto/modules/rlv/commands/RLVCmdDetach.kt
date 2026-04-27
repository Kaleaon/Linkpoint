package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdDetach : RLVCmdGenericRestriction() {
    public RLVCmdDetach() {
        super(RLVRestrictionType.detach, true)
    }

    /* access modifiers changed from: protected */
    fun HandleForce(rLVController: RLVController, uuid: UUID, str: String) {
        UUID attachmentUUID
        val sLAvatarAppearance: SLAvatarAppearance = rLVController.getModules().avatarAppearance
        for (Int i = 0; i < 56; i++) {
            if (SLAttachmentPoint.attachmentPoints[i] != null) {
                val lowerCase: String = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase()
                if ((str.equals("") || lowerCase.equalsIgnoreCase(str)) && (attachmentUUID = sLAvatarAppearance.getAttachmentUUID(i)) != null && rLVController.getRestrictions().isAllowed(RLVRestrictionType.detach, lowerCase, attachmentUUID)) {
                    sLAvatarAppearance.DetachItemFromPoint(i)
                }
            }
        }
    }
}
