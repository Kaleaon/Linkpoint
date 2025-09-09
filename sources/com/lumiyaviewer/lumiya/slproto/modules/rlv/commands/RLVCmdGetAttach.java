package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.UUID;

public class RLVCmdGetAttach implements RLVCommand {
    private static final int NUM_ATTACHMENT_POINTS_LSL = 41;

    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        try {
            int parseInt = Integer.parseInt(str);
            SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance;
            String str3 = "";
            for (int i = 0; i < 41; i++) {
                String str4 = "nonexistent";
                if (SLAttachmentPoint.attachmentPoints[i] != null) {
                    str4 = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase();
                }
                if (str2.equals("") || str4.equalsIgnoreCase(str2)) {
                    str3 = sLAvatarAppearance.getAttachmentUUID(i) != null ? str3 + "1" : str3 + "0";
                }
            }
            rLVController.sayOnChannel(parseInt, str3);
        } catch (NumberFormatException e) {
            Debug.Warning(e);
        }
    }
}
