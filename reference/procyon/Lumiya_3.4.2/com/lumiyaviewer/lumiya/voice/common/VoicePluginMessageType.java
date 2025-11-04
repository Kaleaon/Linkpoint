// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common;

public enum VoicePluginMessageType
{
    public static final int VOICE_PLUGIN_MESSAGE = 200;
    
    VoiceAcceptCall, 
    VoiceAudioProperties, 
    VoiceChannelClosed, 
    VoiceChannelStatus, 
    VoiceConnectChannel, 
    VoiceEnableMic, 
    VoiceInitialize, 
    VoiceInitializeReply, 
    VoiceLogin, 
    VoiceLoginStatus, 
    VoiceLogout, 
    VoiceRejectCall, 
    VoiceRinging, 
    VoiceSet3DPosition, 
    VoiceSetAudioProperties, 
    VoiceTerminateCall;
}
