// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands:
//            RLVCmdGenericRestriction

public class RLVCmdSit extends RLVCmdGenericRestriction
{

    public RLVCmdSit()
    {
        super(RLVRestrictionType.sit, false);
    }

    protected void HandleForce(RLVController rlvcontroller, UUID uuid, String s)
    {
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_20;
        }
        uuid = UUID.fromString(s);
        rlvcontroller.getModules().avatarControl.ForceSitOnObject(uuid);
        return;
        rlvcontroller;
        Debug.Warning(rlvcontroller);
        return;
    }
}
