package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdRemoveOutfit : RLVCmdGenericRestriction() {
    public RLVCmdRemoveOutfit() {
        super(RLVRestrictionType.remoutfit, true)
    }

    /* access modifiers changed from: protected */
    fun HandleForce(rLVController: RLVController, uuid: UUID, str: String) {
        val sLAvatarAppearance: SLAvatarAppearance = rLVController.getModules().avatarAppearance
        for (SLWearableType sLWearableType : SLWearableType.values()) {
            if (!sLWearableType.isBodyPart()) {
                val name: String = sLWearableType.getName()
                if (str.equals("") || name.equalsIgnoreCase(str)) {
                    sLAvatarAppearance.ForceTakeItemOff(sLWearableType)
                }
            }
        }
    }
}
