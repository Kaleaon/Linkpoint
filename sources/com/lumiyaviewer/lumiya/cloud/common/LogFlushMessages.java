// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import com.google.common.base.Strings;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable

public class LogFlushMessages
    implements Bundleable
{

    public final String agentName;
    public final UUID agentUUID;
    public final String chatterName;

    public LogFlushMessages(Bundle bundle)
    {
        agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        agentName = Strings.nullToEmpty(bundle.getString("agentName"));
        chatterName = bundle.getString("chatterName");
    }

    public LogFlushMessages(UUID uuid, String s, String s1)
    {
        agentUUID = uuid;
        agentName = s;
        chatterName = s1;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", agentUUID.toString());
        bundle.putString("agentName", agentName);
        bundle.putString("chatterName", chatterName);
        return bundle;
    }
}
