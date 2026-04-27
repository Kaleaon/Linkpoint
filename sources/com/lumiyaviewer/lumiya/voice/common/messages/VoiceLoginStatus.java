// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;

public class VoiceLoginStatus
    implements VoicePluginMessage
{

    public final String errorMessage;
    public final boolean loggedIn;
    public final VoiceLoginInfo voiceLoginInfo;

    public VoiceLoginStatus(Bundle bundle)
    {
        VoiceLoginInfo voicelogininfo = null;
        super();
        Bundle bundle1 = bundle.getBundle("voiceLoginInfo");
        if (bundle1 != null)
        {
            voicelogininfo = new VoiceLoginInfo(bundle1);
        }
        voiceLoginInfo = voicelogininfo;
        loggedIn = bundle.getBoolean("loggedIn");
        errorMessage = bundle.getString("errorMessage");
    }

    public VoiceLoginStatus(VoiceLoginInfo voicelogininfo, boolean flag, String s)
    {
        voiceLoginInfo = voicelogininfo;
        loggedIn = flag;
        errorMessage = s;
    }

    public Bundle toBundle()
    {
        Bundle bundle = null;
        Bundle bundle1 = new Bundle();
        if (voiceLoginInfo != null)
        {
            bundle = voiceLoginInfo.toBundle();
        }
        bundle1.putBundle("voiceLoginInfo", bundle);
        bundle1.putBoolean("loggedIn", loggedIn);
        bundle1.putString("errorMessage", errorMessage);
        return bundle1;
    }
}
