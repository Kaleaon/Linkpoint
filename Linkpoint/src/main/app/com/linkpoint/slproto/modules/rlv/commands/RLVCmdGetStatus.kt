package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import java.util.UUID

class RLVCmdGetStatus : RLVCommand {
    Unit Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        String str3
        String str4
        try {
            Int parseInt = Int.parseInt(str)
            String str5 = str2 != null ? str2 : ""
            Int indexOf = str5.indexOf(59)
            if (indexOf >= 0) {
                String substring = str5.substring(indexOf + 1)
                String substring2 = str5.substring(0, indexOf)
                str3 = substring
                str4 = substring2
            } else {
                str3 = "/"
                str4 = str5
            }
            String lowerCase = str4.toLowerCase()
            String str6 = ""
            for (RLVRestrictionType rLVRestrictionType : rLVController.getRestrictions().getRestrictionsByObject(uuid)) {
                str6 = (lowerCase.equals("") || rLVRestrictionType.toString().indexOf(lowerCase) >= 0) ? str6 + str3 + rLVRestrictionType.toString() : str6
            }
            rLVController.sayOnChannel(parseInt, str6)
        } catch (NumberFormatException e) {
            Debug.Warning(e)
        }
    }
}
