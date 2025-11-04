package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

/* loaded from: classes.dex */
public class RLVCmdRedirChat implements RLVCommand {
    @Override // com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand
    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        if (str2 == null) {
            str2 = "";
        }
        if (str2.equals("")) {
            return;
        }
        try {
            int parseInt = Integer.parseInt(str2);
            if (str.equals("n") || str.equals("add")) {
                rLVController.getRestrictions().addRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(parseInt));
            } else if (str.equals("y") || str.equals("rem")) {
                rLVController.getRestrictions().removeRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(parseInt));
            }
        } catch (NumberFormatException e) {
            Debug.Warning(e);
        }
    }
}
