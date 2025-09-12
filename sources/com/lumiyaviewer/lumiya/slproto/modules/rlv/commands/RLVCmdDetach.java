// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictions;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands:
//            RLVCmdGenericRestriction

public class RLVCmdDetach extends RLVCmdGenericRestriction
{

    public RLVCmdDetach()
    {
        super(RLVRestrictionType.detach, true);
    }

    protected void HandleForce(RLVController rlvcontroller, UUID uuid, String s)
    {
        uuid = rlvcontroller.getModules().avatarAppearance;
        for (int i = 0; i < 56; i++)
        {
            if (SLAttachmentPoint.attachmentPoints[i] == null)
            {
                continue;
            }
            String s1 = SLAttachmentPoint.attachmentPoints[i].name.toLowerCase();
            if (!s.equals("") && !s1.equalsIgnoreCase(s))
            {
                continue;
            }
            UUID uuid1 = uuid.getAttachmentUUID(i);
            if (uuid1 != null && rlvcontroller.getRestrictions().isAllowed(RLVRestrictionType.detach, s1, uuid1))
            {
                uuid.DetachItemFromPoint(i);
            }
        }

    }
}
