// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class ConnectionFragment extends Fragment
{

    public static final String EXTRA_ACTIVE_AGENT_UUID = "activeAgentUUID";

    public ConnectionFragment()
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
}
