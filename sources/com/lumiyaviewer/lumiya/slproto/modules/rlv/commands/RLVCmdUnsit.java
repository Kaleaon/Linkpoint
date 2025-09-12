// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands:
//            RLVCmdGenericRestriction

public class RLVCmdUnsit extends RLVCmdGenericRestriction
{

    public RLVCmdUnsit()
    {
        super(RLVRestrictionType.unsit, false);
    }

    protected void HandleForce(RLVController rlvcontroller, UUID uuid, String s)
    {
        rlvcontroller.getModules().avatarControl.ForceStand();
    }
}
