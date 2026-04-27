// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

public class VoiceChannelStatus
    implements VoicePluginMessage
{

    public final VoiceChannelInfo channelInfo;
    public final VoiceChatInfo chatInfo;
    public final String errorMessage;

    public VoiceChannelStatus(Bundle bundle)
    {
        channelInfo = new VoiceChannelInfo(bundle.getBundle("channelInfo"));
        chatInfo = VoiceChatInfo.create(bundle.getBundle("chatInfo"));
        errorMessage = bundle.getString("errorMessage");
    }

    public VoiceChannelStatus(VoiceChannelInfo voicechannelinfo, VoiceChatInfo voicechatinfo, String s)
    {
        channelInfo = voicechannelinfo;
        chatInfo = voicechatinfo;
        errorMessage = s;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle("channelInfo", channelInfo.toBundle());
        bundle.putBundle("chatInfo", chatInfo.toBundle());
        bundle.putString("errorMessage", errorMessage);
        return bundle;
    }
}
