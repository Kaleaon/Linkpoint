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

    public RLVCmdGenericRestriction(RLVRestrictionType rLVRestrictionType, Boolean z) {
        this.restrictionType = rLVRestrictionType
        this.canHaveExceptions = z
    }

    fun Handle(rLVController: RLVController, uuid: UUID, rLVCommands: RLVCommands, str: String, str2: String) {
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
            val restrictions: RLVRestrictions = rLVController.getRestrictions()
            val rLVRestrictionType: RLVRestrictionType = this.restrictionType
            if (!this.canHaveExceptions) {
                str2 = ""
            }
            restrictions.addRestriction(rLVRestrictionType, uuid, str2)
        } else if (str.equals(str4) || str.equals("rem")) {
            val restrictions2: RLVRestrictions = rLVController.getRestrictions()
            val rLVRestrictionType2: RLVRestrictionType = this.restrictionType
            if (!this.canHaveExceptions) {
                str2 = ""
            }
            restrictions2.removeRestriction(rLVRestrictionType2, uuid, str2)
        } else if (str.equals("force")) {
            HandleForce(rLVController, uuid, str2)
        }
    }

    /* access modifiers changed from: protected */
    fun HandleForce(rLVController: RLVController, uuid: UUID, str: String) {
        Debug.Printf("RLV: force option not supported for restriction '%s'", this.restrictionType.toString())
    }
}
