// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdGetAttach implements RLVCommand
{
    private static final int NUM_ATTACHMENT_POINTS_LSL = 41;
    
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, String lowerCase, final String anotherString) {
        try {
            final int int1 = Integer.parseInt(lowerCase);
            final SLAvatarAppearance avatarAppearance = rlvController.getModules().avatarAppearance;
            int i = 0;
            String s = "";
            while (i < 41) {
                lowerCase = "nonexistent";
                if (SLAttachmentPoint.attachmentPoints[i] != null) {
                    lowerCase = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase();
                }
                String s2 = null;
                Label_0110: {
                    if (!anotherString.equals("")) {
                        s2 = s;
                        if (!lowerCase.equalsIgnoreCase(anotherString)) {
                            break Label_0110;
                        }
                    }
                    if (avatarAppearance.getAttachmentUUID(i) != null) {
                        s2 = s + "1";
                    }
                    else {
                        s2 = s + "0";
                    }
                }
                ++i;
                s = s2;
            }
            rlvController.sayOnChannel(int1, s);
        }
        catch (final NumberFormatException ex) {
            Debug.Warning(ex);
        }
    }
}
