// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceInitializeReply
    implements VoicePluginMessage
{

    public final boolean appVersionOk;
    public final String errorMessage;
    public final int pluginVersionCode;

    public VoiceInitializeReply(int i, String s, boolean flag)
    {
        pluginVersionCode = i;
        errorMessage = s;
        appVersionOk = flag;
    }

    public VoiceInitializeReply(Bundle bundle)
    {
        pluginVersionCode = bundle.getInt("pluginVersionCode");
        errorMessage = bundle.getString("errorMessage");
        appVersionOk = bundle.getBoolean("appVersionOk");
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", pluginVersionCode);
        bundle.putString("errorMessage", errorMessage);
        bundle.putBoolean("appVersionOk", appVersionOk);
        return bundle;
    }
}
