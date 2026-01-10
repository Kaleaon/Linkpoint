package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import java.util.UUID

class RLVCmdGetOutfit : RLVCommand {
    fun Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2)  {
        try {
            var parseInt: Int = Int.parseInt(str)
            SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance
            var str3: String = ""
            for (SLWearableType sLWearableType : SLWearableType[]{SLWearableType.WT_GLOVES, SLWearableType.WT_JACKET, SLWearableType.WT_PANTS, SLWearableType.WT_SHIRT, SLWearableType.WT_SHOES, SLWearableType.WT_SKIRT, SLWearableType.WT_SOCKS, SLWearableType.WT_UNDERPANTS, SLWearableType.WT_UNDERSHIRT, SLWearableType.WT_SKIN, SLWearableType.WT_EYES, SLWearableType.WT_HAIR, SLWearableType.WT_SHAPE, SLWearableType.WT_ALPHA, SLWearableType.WT_TATTOO}) {
                if (!sLWearableType.isBodyPart()) {
                    var name: String = sLWearableType.getName()
                    if (str2.equals("") || name.equalsIgnoreCase(str2)) {
                        str3 = sLAvatarAppearance.hasWornWearable(sLWearableType) ? str3 + "1" : str3 + "0"
                    }
                }
            }
            rLVController.sayOnChannel(parseInt, str3)
        } catch (NumberFormatException e) {
            Debug.Warning(e)
        }
    }
}
