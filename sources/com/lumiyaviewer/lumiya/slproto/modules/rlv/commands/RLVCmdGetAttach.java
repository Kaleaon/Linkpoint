// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.UUID;

public class RLVCmdGetAttach
    implements RLVCommand
{

    private static final int NUM_ATTACHMENT_POINTS_LSL = 41;

    public RLVCmdGetAttach()
    {
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        SLAvatarAppearance slavatarappearance;
        int j;
        j = Integer.parseInt(s);
        slavatarappearance = rlvcontroller.getModules().avatarAppearance;
        int i;
        i = 0;
        rlvcommands = "";
_L2:
        if (i >= 41)
        {
            break MISSING_BLOCK_LABEL_134;
        }
        s = "nonexistent";
        if (SLAttachmentPoint.attachmentPoints[i] != null)
        {
            s = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase();
        }
        if (s1.equals(""))
        {
            break MISSING_BLOCK_LABEL_78;
        }
        uuid = rlvcommands;
        if (!s.equalsIgnoreCase(s1))
        {
            break MISSING_BLOCK_LABEL_148;
        }
        if (slavatarappearance.getAttachmentUUID(i) != null)
        {
            uuid = (new StringBuilder()).append(rlvcommands).append("1").toString();
            break MISSING_BLOCK_LABEL_148;
        }
        uuid = (new StringBuilder()).append(rlvcommands).append("0").toString();
        break MISSING_BLOCK_LABEL_148;
        try
        {
            rlvcontroller.sayOnChannel(j, rlvcommands);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (RLVController rlvcontroller)
        {
            Debug.Warning(rlvcontroller);
        }
        return;
        i++;
        rlvcommands = uuid;
        if (true) goto _L2; else goto _L1
_L1:
    }
}
