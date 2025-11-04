// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdRedirChat implements RLVCommand
{
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, final String s, final String s2) {
        String s3 = s2;
        if (s2 == null) {
            s3 = "";
        }
        if (s3.equals("")) {
            return;
        }
        while (true) {
            try {
                final int int1 = Integer.parseInt(s3);
                if (s.equals("n") || s.equals("add")) {
                    rlvController.getRestrictions().addRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(int1));
                }
                else if (s.equals("y") || s.equals("rem")) {
                    rlvController.getRestrictions().removeRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(int1));
                }
            }
            catch (final NumberFormatException ex) {
                Debug.Warning(ex);
            }
        }
    }
}
