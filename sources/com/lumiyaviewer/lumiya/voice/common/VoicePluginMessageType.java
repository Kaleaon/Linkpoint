// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common;


public final class VoicePluginMessageType extends Enum
{

    private static final VoicePluginMessageType $VALUES[];
    public static final int VOICE_PLUGIN_MESSAGE = 200;
    public static final VoicePluginMessageType VoiceAcceptCall;
    public static final VoicePluginMessageType VoiceAudioProperties;
    public static final VoicePluginMessageType VoiceChannelClosed;
    public static final VoicePluginMessageType VoiceChannelStatus;
    public static final VoicePluginMessageType VoiceConnectChannel;
    public static final VoicePluginMessageType VoiceEnableMic;
    public static final VoicePluginMessageType VoiceInitialize;
    public static final VoicePluginMessageType VoiceInitializeReply;
    public static final VoicePluginMessageType VoiceLogin;
    public static final VoicePluginMessageType VoiceLoginStatus;
    public static final VoicePluginMessageType VoiceLogout;
    public static final VoicePluginMessageType VoiceRejectCall;
    public static final VoicePluginMessageType VoiceRinging;
    public static final VoicePluginMessageType VoiceSet3DPosition;
    public static final VoicePluginMessageType VoiceSetAudioProperties;
    public static final VoicePluginMessageType VoiceTerminateCall;

    private VoicePluginMessageType(String s, int i)
    {
        super(s, i);
    }

    public static VoicePluginMessageType valueOf(String s)
    {
        return (VoicePluginMessageType)Enum.valueOf(com/lumiyaviewer/lumiya/voice/common/VoicePluginMessageType, s);
    }

    public static VoicePluginMessageType[] values()
    {
        return (VoicePluginMessageType[])$VALUES.clone();
    }

    static 
    {
        VoiceInitialize = new VoicePluginMessageType("VoiceInitialize", 0);
        VoiceInitializeReply = new VoicePluginMessageType("VoiceInitializeReply", 1);
        VoiceLogin = new VoicePluginMessageType("VoiceLogin", 2);
        VoiceLogout = new VoicePluginMessageType("VoiceLogout", 3);
        VoiceConnectChannel = new VoicePluginMessageType("VoiceConnectChannel", 4);
        VoiceLoginStatus = new VoicePluginMessageType("VoiceLoginStatus", 5);
        VoiceChannelStatus = new VoicePluginMessageType("VoiceChannelStatus", 6);
        VoiceSet3DPosition = new VoicePluginMessageType("VoiceSet3DPosition", 7);
        VoiceRinging = new VoicePluginMessageType("VoiceRinging", 8);
        VoiceAcceptCall = new VoicePluginMessageType("VoiceAcceptCall", 9);
        VoiceRejectCall = new VoicePluginMessageType("VoiceRejectCall", 10);
        VoiceChannelClosed = new VoicePluginMessageType("VoiceChannelClosed", 11);
        VoiceTerminateCall = new VoicePluginMessageType("VoiceTerminateCall", 12);
        VoiceEnableMic = new VoicePluginMessageType("VoiceEnableMic", 13);
        VoiceSetAudioProperties = new VoicePluginMessageType("VoiceSetAudioProperties", 14);
        VoiceAudioProperties = new VoicePluginMessageType("VoiceAudioProperties", 15);
        $VALUES = (new VoicePluginMessageType[] {
            VoiceInitialize, VoiceInitializeReply, VoiceLogin, VoiceLogout, VoiceConnectChannel, VoiceLoginStatus, VoiceChannelStatus, VoiceSet3DPosition, VoiceRinging, VoiceAcceptCall, 
            VoiceRejectCall, VoiceChannelClosed, VoiceTerminateCall, VoiceEnableMic, VoiceSetAudioProperties, VoiceAudioProperties
        });
    }
}
