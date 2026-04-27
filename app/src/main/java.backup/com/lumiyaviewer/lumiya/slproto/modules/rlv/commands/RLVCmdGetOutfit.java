package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.UUID;

public class RLVCmdGetOutfit implements RLVCommand {
    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        try {
            int parseInt = Integer.parseInt(str);
            SLAvatarAppearance sLAvatarAppearance = rLVController.getModules().avatarAppearance;
            String str3 = "";
            for (SLWearableType sLWearableType : new SLWearableType[]{SLWearableType.WT_GLOVES, SLWearableType.WT_JACKET, SLWearableType.WT_PANTS, SLWearableType.WT_SHIRT, SLWearableType.WT_SHOES, SLWearableType.WT_SKIRT, SLWearableType.WT_SOCKS, SLWearableType.WT_UNDERPANTS, SLWearableType.WT_UNDERSHIRT, SLWearableType.WT_SKIN, SLWearableType.WT_EYES, SLWearableType.WT_HAIR, SLWearableType.WT_SHAPE, SLWearableType.WT_ALPHA, SLWearableType.WT_TATTOO}) {
                if (!sLWearableType.isBodyPart()) {
                    String name = sLWearableType.getName();
                    if (str2.equals("") || name.equalsIgnoreCase(str2)) {
                        str3 = sLAvatarAppearance.hasWornWearable(sLWearableType) ? str3 + "1" : str3 + "0";
                    }
                }
            }
            rLVController.sayOnChannel(parseInt, str3);
        } catch (NumberFormatException e) {
            Debug.Warning(e);
        }
    }
}
