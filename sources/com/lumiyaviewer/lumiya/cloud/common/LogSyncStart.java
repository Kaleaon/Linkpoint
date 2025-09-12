// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable

public class LogSyncStart
    implements Bundleable
{

    public final UUID agentUUID;
    public final int appVersionCode;

    public LogSyncStart(int i, UUID uuid)
    {
        appVersionCode = i;
        agentUUID = uuid;
    }

    public LogSyncStart(Bundle bundle)
    {
        appVersionCode = bundle.getInt("appVersionCode");
        agentUUID = UUID.fromString(bundle.getString("agentUUID"));
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("appVersionCode", appVersionCode);
        bundle.putString("agentUUID", agentUUID.toString());
        return bundle;
    }
}
