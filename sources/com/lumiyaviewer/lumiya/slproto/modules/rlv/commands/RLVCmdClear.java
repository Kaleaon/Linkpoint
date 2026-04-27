// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictions;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RLVCmdClear
    implements RLVCommand
{

    public RLVCmdClear()
    {
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        rlvcommands = new HashSet();
        s1 = RLVRestrictionType.values();
        int i = 0;
        int j = s1.length;
        while (i < j) 
        {
            RLVRestrictionType rlvrestrictiontype = s1[i];
            if (s == "")
            {
                rlvcommands.add(rlvrestrictiontype);
            } else
            if (rlvrestrictiontype.toString().contains(s))
            {
                rlvcommands.add(rlvrestrictiontype);
            }
            i++;
        }
        rlvcontroller.getRestrictions().removeRestrictions(uuid, rlvcommands);
    }
}
