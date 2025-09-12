// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;

public class VoiceSet3DPosition
    implements VoicePluginMessage
{

    public final Voice3DPosition listenerPosition;
    public final Voice3DPosition speakerPosition;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceSet3DPosition(Bundle bundle)
    {
        voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        speakerPosition = new Voice3DPosition(bundle.getBundle("speakerPosition"));
        listenerPosition = new Voice3DPosition(bundle.getBundle("listenerPosition"));
    }

    public VoiceSet3DPosition(VoiceChannelInfo voicechannelinfo, Voice3DPosition voice3dposition, Voice3DPosition voice3dposition1)
    {
        voiceChannelInfo = voicechannelinfo;
        speakerPosition = voice3dposition;
        listenerPosition = voice3dposition1;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle());
        bundle.putBundle("speakerPosition", speakerPosition.toBundle());
        bundle.putBundle("listenerPosition", listenerPosition.toBundle());
        return bundle;
    }
}
