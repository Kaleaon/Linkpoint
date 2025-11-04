// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdClear implements RLVCommand
{
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, final String s, final String s2) {
        final HashSet set = new HashSet();
        final RLVRestrictionType[] values = RLVRestrictionType.values();
        for (int i = 0; i < values.length; ++i) {
            final RLVRestrictionType rlvRestrictionType = values[i];
            if (s == "") {
                set.add(rlvRestrictionType);
            }
            else if (rlvRestrictionType.toString().contains(s)) {
                set.add(rlvRestrictionType);
            }
        }
        rlvController.getRestrictions().removeRestrictions(uuid, set);
    }
}
