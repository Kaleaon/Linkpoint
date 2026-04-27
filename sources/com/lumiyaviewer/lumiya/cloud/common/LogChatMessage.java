// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            Bundleable

public class LogChatMessage
    implements Bundleable
{

    public final String chatterName;
    public final int chatterType;
    public final UUID chatterUUID;
    public final long messageID;
    public final String messageText;

    public LogChatMessage(int i, UUID uuid, long l, String s, String s1)
    {
        chatterType = i;
        chatterUUID = uuid;
        messageID = l;
        chatterName = s;
        messageText = s1;
    }

    public LogChatMessage(Bundle bundle)
    {
        chatterType = bundle.getInt("chatterType");
        UUID uuid;
        if (!bundle.containsKey("chatterUUID"))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(bundle.getString("chatterUUID"));
        }
        chatterUUID = uuid;
        messageID = bundle.getLong("messageID");
        chatterName = bundle.getString("chatterName");
        messageText = bundle.getString("messageText");
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("chatterType", chatterType);
        if (chatterUUID != null)
        {
            bundle.putString("chatterUUID", chatterUUID.toString());
        }
        bundle.putLong("messageID", messageID);
        bundle.putString("chatterName", chatterName);
        bundle.putString("messageText", messageText);
        return bundle;
    }
}
