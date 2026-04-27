// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv.commands;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommand;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVCommands;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.UUID;

public class RLVCmdGetOutfit
    implements RLVCommand
{

    public RLVCmdGetOutfit()
    {
    }

    public void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1)
    {
        SLWearableType aslwearabletype[];
        int j;
        int k;
        j = Integer.parseInt(s);
        s = rlvcontroller.getModules().avatarAppearance;
        aslwearabletype = new SLWearableType[15];
        aslwearabletype[0] = SLWearableType.WT_GLOVES;
        aslwearabletype[1] = SLWearableType.WT_JACKET;
        aslwearabletype[2] = SLWearableType.WT_PANTS;
        aslwearabletype[3] = SLWearableType.WT_SHIRT;
        aslwearabletype[4] = SLWearableType.WT_SHOES;
        aslwearabletype[5] = SLWearableType.WT_SKIRT;
        aslwearabletype[6] = SLWearableType.WT_SOCKS;
        aslwearabletype[7] = SLWearableType.WT_UNDERPANTS;
        aslwearabletype[8] = SLWearableType.WT_UNDERSHIRT;
        aslwearabletype[9] = SLWearableType.WT_SKIN;
        aslwearabletype[10] = SLWearableType.WT_EYES;
        aslwearabletype[11] = SLWearableType.WT_HAIR;
        aslwearabletype[12] = SLWearableType.WT_SHAPE;
        aslwearabletype[13] = SLWearableType.WT_ALPHA;
        aslwearabletype[14] = SLWearableType.WT_TATTOO;
        k = aslwearabletype.length;
        int i;
        rlvcommands = "";
        i = 0;
_L2:
        SLWearableType slwearabletype;
        if (i >= k)
        {
            break MISSING_BLOCK_LABEL_257;
        }
        slwearabletype = aslwearabletype[i];
        uuid = rlvcommands;
        String s2;
        if (slwearabletype.isBodyPart())
        {
            break MISSING_BLOCK_LABEL_271;
        }
        s2 = slwearabletype.getName();
        if (s1.equals(""))
        {
            break MISSING_BLOCK_LABEL_201;
        }
        uuid = rlvcommands;
        if (!s2.equalsIgnoreCase(s1))
        {
            break MISSING_BLOCK_LABEL_271;
        }
        if (s.hasWornWearable(slwearabletype))
        {
            uuid = (new StringBuilder()).append(rlvcommands).append("1").toString();
            break MISSING_BLOCK_LABEL_271;
        }
        uuid = (new StringBuilder()).append(rlvcommands).append("0").toString();
        break MISSING_BLOCK_LABEL_271;
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
