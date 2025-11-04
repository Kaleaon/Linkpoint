// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictions;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;

public class RLVCmdGenericRestriction implements RLVCommand
{
    private boolean canHaveExceptions;
    private RLVRestrictionType restrictionType;
    
    public RLVCmdGenericRestriction(final RLVRestrictionType restrictionType, final boolean canHaveExceptions) {
        this.restrictionType = restrictionType;
        this.canHaveExceptions = canHaveExceptions;
    }
    
    @Override
    public void Handle(final RLVController rlvController, final UUID uuid, final RLVCommands rlvCommands, final String s, String anObject) {
        String s2 = anObject;
        if (anObject == null) {
            s2 = "";
        }
        String anObject2;
        if (this.restrictionType.getRuleMatchType() == RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance) {
            anObject2 = "y";
            anObject = "n";
        }
        else {
            anObject2 = "n";
            anObject = "y";
        }
        if (s.equals(anObject2) || s.equals("add")) {
            final RLVRestrictions restrictions = rlvController.getRestrictions();
            final RLVRestrictionType restrictionType = this.restrictionType;
            if (!this.canHaveExceptions) {
                s2 = "";
            }
            restrictions.addRestriction(restrictionType, uuid, s2);
        }
        else if (s.equals(anObject) || s.equals("rem")) {
            final RLVRestrictions restrictions2 = rlvController.getRestrictions();
            final RLVRestrictionType restrictionType2 = this.restrictionType;
            if (!this.canHaveExceptions) {
                s2 = "";
            }
            restrictions2.removeRestriction(restrictionType2, uuid, s2);
        }
        else if (s.equals("force")) {
            this.HandleForce(rlvController, uuid, s2);
        }
    }
    
    protected void HandleForce(final RLVController rlvController, final UUID uuid, final String s) {
        Debug.Printf("RLV: force option not supported for restriction '%s'", this.restrictionType.toString());
    }
}
