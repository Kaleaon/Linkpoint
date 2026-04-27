// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdTeleportTo implements RLVCommand
{
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, final String s, final String s2) {
        if (!s.equals("force") || s2 == null) {
            return;
        }
        final String[] split = s2.split("/");
        if (split.length < 3) {
            return;
        }
        try {
            rlvController.teleportToGlobalPos(uuid, new LLVector3(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2])));
        }
        catch (final NumberFormatException ex) {
            Debug.Warning(ex);
        }
    }
}
