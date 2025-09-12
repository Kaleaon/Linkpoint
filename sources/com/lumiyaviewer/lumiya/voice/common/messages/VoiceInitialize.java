// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceInitialize
    implements VoicePluginMessage
{

    public final int appVersionCode;

    public VoiceInitialize(int i)
    {
        appVersionCode = i;
    }

    public VoiceInitialize(Bundle bundle)
    {
        appVersionCode = bundle.getInt("appVersionCode");
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("appVersionCode", appVersionCode);
        return bundle;
    }
}
