// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;

public class RLVCmdSit extends RLVCmdGenericRestriction
{
    public RLVCmdSit() {
        super(RLVRestrictionType.sit, false);
    }
    
    @Override
    protected void HandleForce(final RLVController rlvController, UUID fromString, final String name) {
        if (name == null) {
            return;
        }
        try {
            fromString = UUID.fromString(name);
            rlvController.getModules().avatarControl.ForceSitOnObject(fromString);
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
        }
    }
}
