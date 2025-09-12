// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.net.Uri;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import java.util.UUID;

public class VoiceRinging
    implements VoicePluginMessage
{

    public final UUID agentUUID;
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceRinging(Uri uri)
    {
        UUID uuid = null;
        super();
        sessionHandle = uri.getQueryParameter("sessionHandle");
        String s = uri.getQueryParameter("agentUUID");
        if (s != null)
        {
            uuid = UUID.fromString(s);
        }
        agentUUID = uuid;
        voiceChannelInfo = new VoiceChannelInfo(uri);
    }

    public VoiceRinging(Bundle bundle)
    {
        Object obj = null;
        super();
        sessionHandle = bundle.getString("sessionHandle");
        voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        bundle = bundle.getString("agentUUID");
        if (bundle == null)
        {
            bundle = obj;
        } else
        {
            bundle = UUID.fromString(bundle);
        }
        agentUUID = bundle;
    }

    public VoiceRinging(String s, VoiceChannelInfo voicechannelinfo, UUID uuid)
    {
        sessionHandle = s;
        voiceChannelInfo = voicechannelinfo;
        agentUUID = uuid;
    }

    public Bundle toBundle()
    {
        String s = null;
        Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", sessionHandle);
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle());
        if (agentUUID != null)
        {
            s = agentUUID.toString();
        }
        bundle.putString("agentUUID", s);
        return bundle;
    }

    public Uri toUri()
    {
        android.net.Uri.Builder builder = (new android.net.Uri.Builder()).scheme("com.lumiyaviewer.lumiya").authority("voice").appendQueryParameter("sessionHandle", sessionHandle);
        if (agentUUID != null)
        {
            builder.appendQueryParameter("agentUUID", agentUUID.toString());
        }
        voiceChannelInfo.appendToUri(builder);
        return builder.build();
    }
}
