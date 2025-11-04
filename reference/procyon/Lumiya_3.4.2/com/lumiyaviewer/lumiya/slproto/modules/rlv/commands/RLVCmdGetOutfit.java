// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdGetOutfit implements RLVCommand
{
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, final String s, final String anotherString) {
        try {
            final int int1 = Integer.parseInt(s);
            final SLAvatarAppearance avatarAppearance = rlvController.getModules().avatarAppearance;
            final SLWearableType[] array = { SLWearableType.WT_GLOVES, SLWearableType.WT_JACKET, SLWearableType.WT_PANTS, SLWearableType.WT_SHIRT, SLWearableType.WT_SHOES, SLWearableType.WT_SKIRT, SLWearableType.WT_SOCKS, SLWearableType.WT_UNDERPANTS, SLWearableType.WT_UNDERSHIRT, SLWearableType.WT_SKIN, SLWearableType.WT_EYES, SLWearableType.WT_HAIR, SLWearableType.WT_SHAPE, SLWearableType.WT_ALPHA, SLWearableType.WT_TATTOO };
            final int length = array.length;
            String s2 = "";
            String s3;
            for (int i = 0; i < length; ++i, s2 = s3) {
                final SLWearableType slWearableType = array[i];
                s3 = s2;
                if (!slWearableType.isBodyPart()) {
                    final String name = slWearableType.getName();
                    if (!anotherString.equals("")) {
                        s3 = s2;
                        if (!name.equalsIgnoreCase(anotherString)) {
                            continue;
                        }
                    }
                    if (avatarAppearance.hasWornWearable(slWearableType)) {
                        s3 = s2 + "1";
                    }
                    else {
                        s3 = s2 + "0";
                    }
                }
            }
            rlvController.sayOnChannel(int1, s2);
        }
        catch (final NumberFormatException ex) {
            Debug.Warning(ex);
        }
    }
}
