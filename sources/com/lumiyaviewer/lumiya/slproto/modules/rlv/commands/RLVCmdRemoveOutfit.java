// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands:
//            RLVCmdGenericRestriction

public class RLVCmdRemoveOutfit extends RLVCmdGenericRestriction
{

    public RLVCmdRemoveOutfit()
    {
        super(RLVRestrictionType.remoutfit, true);
    }

    protected void HandleForce(RLVController rlvcontroller, UUID uuid, String s)
    {
        rlvcontroller = rlvcontroller.getModules().avatarAppearance;
        uuid = SLWearableType.values();
        int i = 0;
        for (int j = uuid.length; i < j; i++)
        {
            SLWearableType slwearabletype = uuid[i];
            if (slwearabletype.isBodyPart())
            {
                continue;
            }
            String s1 = slwearabletype.getName();
            if (s.equals("") || s1.equalsIgnoreCase(s))
            {
                rlvcontroller.ForceTakeItemOff(slwearabletype);
            }
        }

    }
}
