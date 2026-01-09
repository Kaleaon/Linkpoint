package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.modules.SLAvatarAppearance
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import java.util.UUID

class RLVCmdGetAttach : RLVCommand {
    private val NUM_ATTACHMENT_POINTS_LSL: Int = 41

    fun Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2)  {
        try {
            var parseInt: Int = Int.parseInt(str)
            SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance
            var str3: String = ""
            for (i in 0 until 41) {
                var str4: String = "nonexistent"
                if (SLAttachmentPoint.attachmentPoints[i] != null) {
                    str4 = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase()
                }
                if (str2.equals("") || str4.equalsIgnoreCase(str2)) {
                    str3 = sLAvatarAppearance.getAttachmentUUID(i) != null ? str3 + "1" : str3 + "0"
                }
            }
            rLVController.sayOnChannel(parseInt, str3)
        } catch (NumberFormatException e) {
            Debug.Warning(e)
        }
    }
}
