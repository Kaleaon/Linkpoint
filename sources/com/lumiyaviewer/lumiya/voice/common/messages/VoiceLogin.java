// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;

public class VoiceLogin
    implements VoicePluginMessage
{

    public final VoiceLoginInfo voiceLoginInfo;

    public VoiceLogin(Bundle bundle)
    {
        voiceLoginInfo = new VoiceLoginInfo(bundle.getBundle("voiceLoginInfo"));
    }

    public VoiceLogin(VoiceLoginInfo voicelogininfo)
    {
        voiceLoginInfo = voicelogininfo;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceLoginInfo", voiceLoginInfo.toBundle());
        return bundle;
    }
}
