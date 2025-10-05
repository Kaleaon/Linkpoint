package com.linkpoint.voice.common

enum class VoicePluginMessageType {
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
    VoiceAudioProperties

    const val Int VOICE_PLUGIN_MESSAGE = 200
}

