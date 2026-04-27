package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

public class RLVCmdGetStatus implements RLVCommand {
    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        String str3;
        String str4;
        try {
            int parseInt = Integer.parseInt(str);
            String str5 = str2 != null ? str2 : "";
            int indexOf = str5.indexOf(59);
            if (indexOf >= 0) {
                String substring = str5.substring(indexOf + 1);
                String substring2 = str5.substring(0, indexOf);
                str3 = substring;
                str4 = substring2;
            } else {
                str3 = "/";
                str4 = str5;
            }
            String lowerCase = str4.toLowerCase();
            String str6 = "";
            for (RLVRestrictionType rLVRestrictionType : rLVController.getRestrictions().getRestrictionsByObject(uuid)) {
                str6 = (lowerCase.equals("") || rLVRestrictionType.toString().indexOf(lowerCase) >= 0) ? str6 + str3 + rLVRestrictionType.toString() : str6;
            }
            rLVController.sayOnChannel(parseInt, str6);
        } catch (NumberFormatException e) {
            Debug.Warning(e);
        }
    }
}
