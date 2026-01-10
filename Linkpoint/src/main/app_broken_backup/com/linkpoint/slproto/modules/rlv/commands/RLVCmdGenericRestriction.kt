package com.linkpoint.slproto.modules.rlv.commands

import com.linkpoint.Debug
import com.linkpoint.slproto.modules.rlv.RLVCommand
import com.linkpoint.slproto.modules.rlv.RLVCommands
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.modules.rlv.RLVRestrictionType
import com.linkpoint.slproto.modules.rlv.RLVRestrictions
import java.util.UUID

class RLVCmdGenericRestriction : RLVCommand {
    private Boolean canHaveExceptions
    private RLVRestrictionType restrictionType

    RLVCmdGenericRestriction(RLVRestrictionType rLVRestrictionType, Boolean z) {
        this.restrictionType = rLVRestrictionType
        this.canHaveExceptions = z
    }

    fun Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2)  {
        String str3
        String str4
        if (str2 == null) {
            str2 = ""
        }
        if (this.restrictionType.getRuleMatchType() == RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance) {
            str3 = "y"
            str4 = "n"
        } else {
            str3 = "n"
            str4 = "y"
        }
        if (str.equals(str3) || str.equals("add")) {
            RLVRestrictions restrictions = rLVController.getRestrictions()
            RLVRestrictionType rLVRestrictionType = this.restrictionType
            if (!this.canHaveExceptions) {
                str2 = ""
            }
            restrictions.addRestriction(rLVRestrictionType, uuid, str2)
        } else if (str.equals(str4) || str.equals("rem")) {
            RLVRestrictions restrictions2 = rLVController.getRestrictions()
            RLVRestrictionType rLVRestrictionType2 = this.restrictionType
            if (!this.canHaveExceptions) {
                str2 = ""
            }
            restrictions2.removeRestriction(rLVRestrictionType2, uuid, str2)
        } else if (str.equals("force")) {
            HandleForce(rLVController, uuid, str2)
        }
    }

    /* access modifiers changed from: protected */
    fun HandleForce(RLVController rLVController, UUID uuid, String str)  {
        Debug.Printf("RLV: force option not supported for restriction '%s'", this.restrictionType.toString())
    }
}
