// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;

public class VoiceConnectChannel
    implements VoicePluginMessage
{

    public final String channelCredentials;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceConnectChannel(Bundle bundle)
    {
        voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        channelCredentials = bundle.getString("channelCredentials");
    }

    public VoiceConnectChannel(VoiceChannelInfo voicechannelinfo, String s)
    {
        voiceChannelInfo = voicechannelinfo;
        channelCredentials = s;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle());
        bundle.putString("channelCredentials", channelCredentials);
        return bundle;
    }
}
