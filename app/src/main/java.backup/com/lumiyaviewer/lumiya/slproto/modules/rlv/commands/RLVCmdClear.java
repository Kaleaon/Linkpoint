package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.HashSet;
import java.util.UUID;

public class RLVCmdClear implements RLVCommand {
    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        HashSet hashSet = new HashSet();
        for (RLVRestrictionType rLVRestrictionType : RLVRestrictionType.values()) {
            if (str == "") {
                hashSet.add(rLVRestrictionType);
            } else if (rLVRestrictionType.toString().contains(str)) {
                hashSet.add(rLVRestrictionType);
            }
        }
        rLVController.getRestrictions().removeRestrictions(uuid, hashSet);
    }
}
