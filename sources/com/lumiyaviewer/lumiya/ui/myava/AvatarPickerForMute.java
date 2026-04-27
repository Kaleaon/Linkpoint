// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import java.util.UUID;

public class AvatarPickerForMute extends AvatarPickerFragment
{

    public AvatarPickerForMute()
    {
    }

    static Bundle makeArguments(UUID uuid)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    public String getTitle()
    {
        return getString(0x7f0902f0);
    }

    protected void onAvatarSelected(ChatterID chatterid, String s)
    {
        Object obj = ActivityUtils.getUserManager(getArguments());
        if (obj != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                ((SLAgentCircuit) (obj)).getModules().muteList.Block(new MuteListEntry(MuteType.AGENT, chatterid.getOptionalChatterUUID(), s, 15));
            }
            chatterid = getActivity();
            if (chatterid instanceof DetailsActivity)
            {
                ((DetailsActivity)chatterid).closeDetailsFragment(this);
            }
        }
    }
}
