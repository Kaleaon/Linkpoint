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
import java.util.Iterator;
import java.util.UUID;

public class RLVCmdGetStatus
    implements RLVCommand
{

    public RLVCmdGetStatus()
    {
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        int i = Integer.parseInt(s);
        int j;
        if (s1 != null)
        {
            rlvcommands = s1;
        } else
        {
            rlvcommands = "";
        }
        j = rlvcommands.indexOf(';');
        if (j < 0) goto _L2; else goto _L1
_L1:
        s = rlvcommands.substring(j + 1);
        s1 = rlvcommands.substring(0, j);
        rlvcommands = s;
        s = s1;
_L4:
        s = s.toLowerCase();
        s1 = rlvcontroller.getRestrictions().getRestrictionsByObject(uuid);
        uuid = "";
        s1 = s1.iterator();
        do
        {
            if (!s1.hasNext())
            {
                break;
            }
            RLVRestrictionType rlvrestrictiontype = (RLVRestrictionType)s1.next();
            if (s.equals("") || rlvrestrictiontype.toString().indexOf(s) >= 0)
            {
                uuid = (new StringBuilder()).append(uuid).append(rlvcommands).append(rlvrestrictiontype.toString()).toString();
            }
        } while (true);
        try
        {
            rlvcontroller.sayOnChannel(i, uuid);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (RLVController rlvcontroller)
        {
            Debug.Warning(rlvcontroller);
        }
        return;
_L2:
        s1 = "/";
        s = rlvcommands;
        rlvcommands = s1;
        if (true) goto _L4; else goto _L3
_L3:
    }
}
