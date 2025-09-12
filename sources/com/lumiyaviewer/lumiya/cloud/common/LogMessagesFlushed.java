// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import java.util.Collection;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable

public class LogMessagesFlushed
    implements Bundleable
{

    public final UUID agentUUID;
    public final ImmutableList messageIDs;

    public LogMessagesFlushed(Bundle bundle)
    {
        agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        bundle = bundle.getLongArray("messageIDs");
        if (bundle == null)
        {
            bundle = new long[0];
        }
        messageIDs = ImmutableList.copyOf(Longs.asList(bundle));
    }

    public LogMessagesFlushed(UUID uuid, Collection collection)
    {
        agentUUID = uuid;
        messageIDs = ImmutableList.copyOf(collection);
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", agentUUID.toString());
        bundle.putLongArray("messageIDs", Longs.toArray(messageIDs));
        return bundle;
    }
}
