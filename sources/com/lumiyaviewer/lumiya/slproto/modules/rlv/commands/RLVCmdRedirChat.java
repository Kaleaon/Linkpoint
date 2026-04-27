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

public class RLVCmdRedirChat
    implements RLVCommand
{

    public RLVCmdRedirChat()
    {
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        rlvcommands = s1;
        if (s1 == null)
        {
            rlvcommands = "";
        }
        if (rlvcommands.equals(""))
        {
            return;
        }
        int i;
        i = Integer.parseInt(rlvcommands);
        if (s.equals("n") || s.equals("add"))
        {
            rlvcontroller.getRestrictions().addRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(i));
            return;
        }
        try
        {
            if (s.equals("y") || s.equals("rem"))
            {
                rlvcontroller.getRestrictions().removeRestriction(RLVRestrictionType.redirchat, uuid, Integer.toString(i));
                return;
            }
        }
        // Misplaced declaration of an exception variable
        catch (RLVController rlvcontroller)
        {
            Debug.Warning(rlvcontroller);
        }
        return;
    }
}
