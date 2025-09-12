// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictions;
import java.util.UUID;

public class RLVCmdGenericRestriction
    implements RLVCommand
{

    private boolean canHaveExceptions;
    private RLVRestrictionType restrictionType;

    public RLVCmdGenericRestriction(RLVRestrictionType rlvrestrictiontype, boolean flag)
    {
        restrictionType = rlvrestrictiontype;
        canHaveExceptions = flag;
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        rlvcommands = s1;
        if (s1 == null)
        {
            rlvcommands = "";
        }
        String s2;
        if (restrictionType.getRuleMatchType() == com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance)
        {
            s2 = "y";
            s1 = "n";
        } else
        {
            s2 = "n";
            s1 = "y";
        }
        if (s.equals(s2) || s.equals("add"))
        {
            rlvcontroller = rlvcontroller.getRestrictions();
            s = restrictionType;
            if (!canHaveExceptions)
            {
                rlvcommands = "";
            }
            rlvcontroller.addRestriction(s, uuid, rlvcommands);
        } else
        {
            if (s.equals(s1) || s.equals("rem"))
            {
                rlvcontroller = rlvcontroller.getRestrictions();
                s = restrictionType;
                if (!canHaveExceptions)
                {
                    rlvcommands = "";
                }
                rlvcontroller.removeRestriction(s, uuid, rlvcommands);
                return;
            }
            if (s.equals("force"))
            {
                HandleForce(rlvcontroller, uuid, rlvcommands);
                return;
            }
        }
    }

    protected void HandleForce(RLVController rlvcontroller, UUID uuid, String s)
    {
        Debug.Printf("RLV: force option not supported for restriction '%s'", new Object[] {
            restrictionType.toString()
        });
    }
}
