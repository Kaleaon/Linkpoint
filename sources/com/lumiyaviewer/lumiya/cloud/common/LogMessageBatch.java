// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable, LogChatMessage

public class LogMessageBatch
    implements Bundleable
{

    public final String agentName;
    public final UUID agentUUID;
    public final long lastMessageID;
    public final ImmutableList messages;

    public LogMessageBatch(Bundle bundle)
    {
        agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        agentName = bundle.getString("agentName");
        lastMessageID = bundle.getLong("lastMessageID");
        bundle = bundle.getParcelableArray("messages");
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        if (bundle != null)
        {
            int j = bundle.length;
            int i = 0;
            while (i < j) 
            {
                Object obj = bundle[i];
                if (obj instanceof Bundle)
                {
                    builder.add(new LogChatMessage((Bundle)obj));
                }
                i++;
            }
        }
        messages = builder.build();
    }

    public LogMessageBatch(UUID uuid, String s, List list, long l)
    {
        agentUUID = uuid;
        agentName = s;
        messages = ImmutableList.copyOf(list);
        lastMessageID = l;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("agentUUID", agentUUID.toString());
        bundle.putString("agentName", agentName);
        bundle.putLong("lastMessageID", lastMessageID);
        Bundle abundle[] = new Bundle[messages.size()];
        int i = 0;
        do
        {
            if (i >= messages.size())
            {
                bundle.putParcelableArray("messages", abundle);
                return bundle;
            }
            abundle[i] = ((LogChatMessage)messages.get(i)).toBundle();
            i++;
        } while (true);
    }
}
