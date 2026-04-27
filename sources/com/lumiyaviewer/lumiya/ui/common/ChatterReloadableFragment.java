// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ChatterFragment, ReloadableFragment, ActivityUtils

public abstract class ChatterReloadableFragment extends ChatterFragment
    implements ReloadableFragment
{

    public ChatterReloadableFragment()
    {
    }

    public void setFragmentArgs(Intent intent, Bundle bundle)
    {
        ChatterID chatterid = null;
        if (bundle != null)
        {
            chatterid = (ChatterID)bundle.getParcelable("chatterID");
        }
        ChatterID chatterid1 = chatterid;
        if (chatterid == null)
        {
            intent = ActivityUtils.getActiveAgentID(intent);
            chatterid1 = chatterid;
            if (intent != null)
            {
                chatterid1 = ChatterID.getLocalChatterID(intent);
            }
        }
        if (bundle != null)
        {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted())
        {
            setNewUser(chatterid1);
        }
    }
}
