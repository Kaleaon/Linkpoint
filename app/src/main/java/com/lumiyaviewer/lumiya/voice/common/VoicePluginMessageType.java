/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice.common;

public enum VoicePluginMessageType {
    VoiceInitialize,
    VoiceInitializeReply,
    VoiceLogin,
    VoiceLogout,
    VoiceConnectChannel,
    VoiceLoginStatus,
    VoiceChannelStatus,
    VoiceSet3DPosition,
    VoiceRinging,
    VoiceAcceptCall,
    VoiceRejectCall,
    VoiceChannelClosed,
    VoiceTerminateCall,
    VoiceEnableMic,
    VoiceSetAudioProperties,
    VoiceAudioProperties;

    public static final int VOICE_PLUGIN_MESSAGE = 200;
}

