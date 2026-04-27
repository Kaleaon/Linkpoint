// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceEnableMic
    implements VoicePluginMessage
{

    public final boolean enableMic;

    public VoiceEnableMic(Bundle bundle)
    {
        enableMic = bundle.getBoolean("enableMic");
    }

    public VoiceEnableMic(boolean flag)
    {
        enableMic = flag;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean("enableMic", enableMic);
        return bundle;
    }
}
