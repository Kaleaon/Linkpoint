// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdUnsit extends RLVCmdGenericRestriction
{
    public RLVCmdUnsit() {
        super(RLVRestrictionType.unsit, false);
    }
    
    @Override
    protected void HandleForce(final RLVController rlvController, final UUID uuid, final String s) {
        rlvController.getModules().avatarControl.ForceStand();
    }
}
