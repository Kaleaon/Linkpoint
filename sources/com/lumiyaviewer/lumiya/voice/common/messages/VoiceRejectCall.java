// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;

public class VoiceRejectCall
    implements VoicePluginMessage
{

    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceRejectCall(Bundle bundle)
    {
        sessionHandle = bundle.getString("sessionHandle");
        voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
    }

    public VoiceRejectCall(String s, VoiceChannelInfo voicechannelinfo)
    {
        sessionHandle = s;
        voiceChannelInfo = voicechannelinfo;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", sessionHandle);
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle());
        return bundle;
    }
}
