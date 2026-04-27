// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;


public final class VoiceBluetoothState extends Enum
{

    private static final VoiceBluetoothState $VALUES[];
    public static final VoiceBluetoothState Active;
    public static final VoiceBluetoothState Connected;
    public static final VoiceBluetoothState Connecting;
    public static final VoiceBluetoothState Disconnected;
    public static final VoiceBluetoothState Error;

    private VoiceBluetoothState(String s, int i)
    {
        super(s, i);
    }

    public static VoiceBluetoothState valueOf(String s)
    {
        return (VoiceBluetoothState)Enum.valueOf(com/lumiyaviewer/lumiya/voice/common/model/VoiceBluetoothState, s);
    }

    public static VoiceBluetoothState[] values()
    {
        return (VoiceBluetoothState[])$VALUES.clone();
    }

    static 
    {
        Disconnected = new VoiceBluetoothState("Disconnected", 0);
        Connecting = new VoiceBluetoothState("Connecting", 1);
        Connected = new VoiceBluetoothState("Connected", 2);
        Active = new VoiceBluetoothState("Active", 3);
        Error = new VoiceBluetoothState("Error", 4);
        $VALUES = (new VoiceBluetoothState[] {
            Disconnected, Connecting, Connected, Active, Error
        });
    }
}
