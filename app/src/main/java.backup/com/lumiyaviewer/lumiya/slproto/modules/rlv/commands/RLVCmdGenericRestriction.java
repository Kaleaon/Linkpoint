package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictions;
import java.util.UUID;

public class RLVCmdGenericRestriction implements RLVCommand {
    private boolean canHaveExceptions;
    private RLVRestrictionType restrictionType;

    public RLVCmdGenericRestriction(RLVRestrictionType rLVRestrictionType, boolean z) {
        this.restrictionType = rLVRestrictionType;
        this.canHaveExceptions = z;
    }

    public void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2) {
        String str3;
        String str4;
        if (str2 == null) {
            str2 = "";
        }
        if (this.restrictionType.getRuleMatchType() == RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance) {
            str3 = "y";
            str4 = "n";
        } else {
            str3 = "n";
            str4 = "y";
        }
        if (str.equals(str3) || str.equals("add")) {
            RLVRestrictions restrictions = rLVController.getRestrictions();
            RLVRestrictionType rLVRestrictionType = this.restrictionType;
            if (!this.canHaveExceptions) {
                str2 = "";
            }
            restrictions.addRestriction(rLVRestrictionType, uuid, str2);
        } else if (str.equals(str4) || str.equals("rem")) {
            RLVRestrictions restrictions2 = rLVController.getRestrictions();
            RLVRestrictionType rLVRestrictionType2 = this.restrictionType;
            if (!this.canHaveExceptions) {
                str2 = "";
            }
            restrictions2.removeRestriction(rLVRestrictionType2, uuid, str2);
        } else if (str.equals("force")) {
            HandleForce(rLVController, uuid, str2);
        }
    }

    /* access modifiers changed from: protected */
    public void HandleForce(RLVController rLVController, UUID uuid, String str) {
        Debug.Printf("RLV: force option not supported for restriction '%s'", this.restrictionType.toString());
    }
}
