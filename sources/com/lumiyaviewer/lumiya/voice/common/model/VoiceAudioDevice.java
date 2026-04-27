// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;


public final class VoiceAudioDevice extends Enum
{

    private static final VoiceAudioDevice $VALUES[];
    public static final VoiceAudioDevice Bluetooth;
    public static final VoiceAudioDevice Default;
    public static final VoiceAudioDevice Loudspeaker;

    private VoiceAudioDevice(String s, int i)
    {
        super(s, i);
    }

    public static VoiceAudioDevice valueOf(String s)
    {
        return (VoiceAudioDevice)Enum.valueOf(com/lumiyaviewer/lumiya/voice/common/model/VoiceAudioDevice, s);
    }

    public static VoiceAudioDevice[] values()
    {
        return (VoiceAudioDevice[])$VALUES.clone();
    }

    static 
    {
        Default = new VoiceAudioDevice("Default", 0);
        Loudspeaker = new VoiceAudioDevice("Loudspeaker", 1);
        Bluetooth = new VoiceAudioDevice("Bluetooth", 2);
        $VALUES = (new VoiceAudioDevice[] {
            Default, Loudspeaker, Bluetooth
        });
    }
}
