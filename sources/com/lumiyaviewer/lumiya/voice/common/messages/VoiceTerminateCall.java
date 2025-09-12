// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;

public class VoiceTerminateCall
    implements VoicePluginMessage
{

    public final VoiceChannelInfo channelInfo;

    public VoiceTerminateCall(Bundle bundle)
    {
        channelInfo = new VoiceChannelInfo(bundle.getBundle("channelInfo"));
    }

    public VoiceTerminateCall(VoiceChannelInfo voicechannelinfo)
    {
        channelInfo = voicechannelinfo;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle("channelInfo", channelInfo.toBundle());
        return bundle;
    }
}
