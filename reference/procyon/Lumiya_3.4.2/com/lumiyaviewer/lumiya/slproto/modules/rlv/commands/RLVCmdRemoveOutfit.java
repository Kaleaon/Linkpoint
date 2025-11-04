// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdRemoveOutfit extends RLVCmdGenericRestriction
{
    public RLVCmdRemoveOutfit() {
        super(RLVRestrictionType.remoutfit, true);
    }
    
    @Override
    protected void HandleForce(final RLVController rlvController, final UUID uuid, final String anotherString) {
        final SLAvatarAppearance avatarAppearance = rlvController.getModules().avatarAppearance;
        final SLWearableType[] values = SLWearableType.values();
        for (int i = 0; i < values.length; ++i) {
            final SLWearableType slWearableType = values[i];
            if (!slWearableType.isBodyPart()) {
                final String name = slWearableType.getName();
                if (anotherString.equals("") || name.equalsIgnoreCase(anotherString)) {
                    avatarAppearance.ForceTakeItemOff(slWearableType);
                }
            }
        }
    }
}
