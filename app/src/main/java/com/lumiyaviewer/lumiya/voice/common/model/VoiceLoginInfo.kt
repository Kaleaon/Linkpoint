package com.lumiyaviewer.lumiya.voice.common.model

import android.os.Bundle
import java.util.*

/**
 * Modern Kotlin VoiceLoginInfo data class
 * Contains login credentials and server information for voice chat
 */
data class VoiceLoginInfo(
    val voiceSipUriHostname: String,
    val voiceAccountServerName: String,
    val agentUUID: UUID,
    val userName: String,
    val password: String
) {
    /**
     * Creates VoiceLoginInfo from a Bundle
     */
    constructor(bundle: Bundle) : this(
        voiceSipUriHostname = bundle.getString("voiceSipUriHostname") ?: "",
        voiceAccountServerName = bundle.getString("voiceAccountServerName") ?: "",
        agentUUID = UUID.fromString(bundle.getString("agentUUID") ?: ""),
        userName = bundle.getString("userName") ?: "",
        password = bundle.getString("password") ?: ""
    )
    
    /**
     * Converts this login info to a Bundle
     */
    fun toBundle(): Bundle = Bundle().apply {
        putString("voiceSipUriHostname", voiceSipUriHostname)
        putString("voiceAccountServerName", voiceAccountServerName)
        putString("agentUUID", agentUUID.toString())
        putString("userName", userName)
        putString("password", password)
    }
}