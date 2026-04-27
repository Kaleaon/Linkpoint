// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice;

public class VoiceSetAudioProperties
    implements VoicePluginMessage
{

    public final VoiceAudioDevice audioDevice;
    public final float speakerVolume;
    public final boolean speakerVolumeValid;

    public VoiceSetAudioProperties(float f, boolean flag, VoiceAudioDevice voiceaudiodevice)
    {
        speakerVolume = f;
        speakerVolumeValid = flag;
        audioDevice = voiceaudiodevice;
    }

    public VoiceSetAudioProperties(Bundle bundle)
    {
        speakerVolumeValid = bundle.containsKey("speakerVolume");
        float f;
        if (!speakerVolumeValid)
        {
            f = (0.0F / 0.0F);
        } else
        {
            f = bundle.getFloat("speakerVolume");
        }
        speakerVolume = f;
        if (!bundle.containsKey("audioDevice"))
        {
            bundle = null;
        } else
        {
            try
            {
                bundle = VoiceAudioDevice.valueOf(bundle.getString("audioDevice"));
            }
            // Misplaced declaration of an exception variable
            catch (Bundle bundle)
            {
                bundle = null;
            }
        }
        audioDevice = bundle;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        if (speakerVolumeValid)
        {
            bundle.putFloat("speakerVolume", speakerVolume);
        }
        if (audioDevice == null)
        {
            return bundle;
        } else
        {
            bundle.putString("audioDevice", audioDevice.name());
            return bundle;
        }
    }
}
