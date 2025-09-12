// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable

public class LogMessagesCompleted
    implements Bundleable
{

    public final UUID agentUUID;
    public final long lastWrittenMessageID;

    public LogMessagesCompleted(Bundle bundle)
    {
        agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        lastWrittenMessageID = bundle.getLong("lastWrittenMessageID");
    }

    public LogMessagesCompleted(UUID uuid, long l)
    {
        agentUUID = uuid;
        lastWrittenMessageID = l;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", agentUUID.toString());
        bundle.putLong("lastWrittenMessageID", lastWrittenMessageID);
        return bundle;
    }
}
