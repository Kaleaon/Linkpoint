// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;

public class VoiceAudioProperties
    implements VoicePluginMessage
{

    public final VoiceBluetoothState bluetoothState;
    public final float speakerVolume;
    public final boolean speakerphoneOn;

    public VoiceAudioProperties(float f, boolean flag, VoiceBluetoothState voicebluetoothstate)
    {
        speakerVolume = f;
        speakerphoneOn = flag;
        bluetoothState = voicebluetoothstate;
    }

    public VoiceAudioProperties(Bundle bundle)
    {
        speakerVolume = bundle.getFloat("speakerVolume");
        speakerphoneOn = bundle.getBoolean("speakerphoneOn");
        try
        {
            bundle = VoiceBluetoothState.valueOf(bundle.getString("bluetoothState"));
        }
        // Misplaced declaration of an exception variable
        catch (Bundle bundle)
        {
            bundle = VoiceBluetoothState.Error;
        }
        bluetoothState = bundle;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putFloat("speakerVolume", speakerVolume);
        bundle.putBoolean("speakerphoneOn", speakerphoneOn);
        bundle.putString("bluetoothState", bluetoothState.name());
        return bundle;
    }
}
