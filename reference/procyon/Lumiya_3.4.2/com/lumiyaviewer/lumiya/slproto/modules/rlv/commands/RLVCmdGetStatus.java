// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import java.util.Iterator;
import java.util.List;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdGetStatus implements RLVCommand
{
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, String s, String substring) {
        while (true) {
            while (true) {
                String s2;
                try {
                    final int int1 = Integer.parseInt(s);
                    if (substring != null) {
                        s2 = substring;
                    }
                    else {
                        s2 = "";
                    }
                    final int index = s2.indexOf(59);
                    if (index >= 0) {
                        substring = s2.substring(index + 1);
                        s = s2.substring(0, index);
                        final String str = substring;
                        s = s.toLowerCase();
                        final List<RLVRestrictionType> restrictionsByObject = rlvController.getRestrictions().getRestrictionsByObject(uuid);
                        String string = "";
                        for (final RLVRestrictionType rlvRestrictionType : restrictionsByObject) {
                            if (s.equals("") || rlvRestrictionType.toString().indexOf(s) >= 0) {
                                string = string + str + rlvRestrictionType.toString();
                            }
                        }
                        rlvController.sayOnChannel(int1, string);
                        return;
                    }
                }
                catch (final NumberFormatException ex) {
                    Debug.Warning(ex);
                    return;
                }
                substring = "/";
                s = s2;
                final String str = substring;
                continue;
            }
        }
    }
}
