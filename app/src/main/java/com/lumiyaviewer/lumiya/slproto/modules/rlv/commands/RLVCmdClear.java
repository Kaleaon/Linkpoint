package com.linkpoint.slproto.modules.rlv.commands;

import com.linkpoint.slproto.modules.rlv.RLVCommand;
import com.linkpoint.slproto.modules.rlv.RLVCommands;
import com.linkpoint.slproto.modules.rlv.RLVController;
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType;
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
