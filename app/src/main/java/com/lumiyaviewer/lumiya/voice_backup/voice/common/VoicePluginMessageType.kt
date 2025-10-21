package com.lumiyaviewer.lumiya.voice.common

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

    companion object {
        const val VOICE_PLUGIN_MESSAGE: Int = 200
    }
}

