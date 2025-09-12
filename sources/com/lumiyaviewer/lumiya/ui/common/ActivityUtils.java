// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class ActivityUtils
{

    public static final String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID";
    public static final String FRAGMENT_SELECTION_KEY = "fragmentSelection";

    public ActivityUtils()
    {
    }

    public static UUID getActiveAgentID(Intent intent)
    {
        if (intent != null)
        {
            intent = intent.getStringExtra("activeAgentUUID");
            if (intent != null)
            {
                return UUIDPool.getUUID(intent);
            }
        }
        return null;
    }

    public static UUID getActiveAgentID(Bundle bundle)
    {
        if (bundle != null)
        {
            bundle = bundle.getString("activeAgentUUID");
            if (bundle != null)
            {
                return UUIDPool.getUUID(bundle);
            }
        }
        return null;
    }

    public static Bundle getFragmentSelection(Bundle bundle)
    {
        if (bundle != null)
        {
            return bundle.getBundle("fragmentSelection");
        } else
        {
            return null;
        }
    }

    public static UserManager getUserManager(Intent intent)
    {
        intent = getActiveAgentID(intent);
        if (intent != null)
        {
            return UserManager.getUserManager(intent);
        } else
        {
            return null;
        }
    }

    public static UserManager getUserManager(Bundle bundle)
    {
        bundle = getActiveAgentID(bundle);
        if (bundle != null)
        {
            return UserManager.getUserManager(bundle);
        } else
        {
            return null;
        }
    }

    public static Bundle makeFragmentArguments(UUID uuid, Bundle bundle)
    {
        Bundle bundle1 = new Bundle();
        if (uuid != null)
        {
            bundle1.putString("activeAgentUUID", uuid.toString());
        }
        if (bundle != null)
        {
            bundle1.putBundle("fragmentSelection", bundle);
        }
        return bundle1;
    }

    public static void setActiveAgentID(Intent intent, UUID uuid)
    {
        if (uuid != null)
        {
            intent.putExtra("activeAgentUUID", uuid.toString());
        }
    }

    public static void setActiveAgentID(Bundle bundle, UUID uuid)
    {
        if (uuid != null)
        {
            bundle.putString("activeAgentUUID", uuid.toString());
        }
    }

    public static void setFragmentSelection(Bundle bundle, Bundle bundle1)
    {
label0:
        {
            if (bundle != null)
            {
                if (bundle1 == null)
                {
                    break label0;
                }
                bundle.putBundle("fragmentSelection", bundle1);
            }
            return;
        }
        bundle.remove("fragmentSelection");
    }
}
