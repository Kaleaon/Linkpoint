// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

public class RLVCmdTeleportTo
    implements RLVCommand
{

    public RLVCmdTeleportTo()
    {
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        if (!s.equals("force") || s1 == null)
        {
            break MISSING_BLOCK_LABEL_59;
        }
        rlvcommands = s1.split("/");
        if (rlvcommands.length < 3)
        {
            break MISSING_BLOCK_LABEL_59;
        }
        rlvcontroller.teleportToGlobalPos(uuid, new LLVector3(Float.parseFloat(rlvcommands[0]), Float.parseFloat(rlvcommands[1]), Float.parseFloat(rlvcommands[2])));
        return;
        rlvcontroller;
        Debug.Warning(rlvcontroller);
        return;
    }
}
