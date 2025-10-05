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

    const val VOICE_PLUGIN_MESSAGE: Int = 200
}

