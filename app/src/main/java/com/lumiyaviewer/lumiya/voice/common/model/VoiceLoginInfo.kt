package com.lumiyaviewer.lumiya.voice.common.model

import android.os.Bundle
import java.util.UUID
import javax.annotation.Nonnull

class VoiceLoginInfo(
    val voiceSipUriHostname: String?,
    val voiceAccountServerName: String?,
    val agentUUID: UUID,
    val userName: String?,
    val password: String?
) {
    constructor(bundle: Bundle) : this(
        bundle.getString("voiceSipUriHostname"),
        bundle.getString("voiceAccountServerName"),
        UUID.fromString(bundle.getString("agentUUID")),
        bundle.getString("userName"),
        bundle.getString("password")
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        
        val that = other as VoiceLoginInfo
        if (voiceSipUriHostname != that.voiceSipUriHostname) return false
        if (voiceAccountServerName != that.voiceAccountServerName) return false
        if (agentUUID != that.agentUUID) return false
        if (userName != that.userName) return false
        return password == that.password
    }

    override fun hashCode(): Int {
        var result = voiceSipUriHostname?.hashCode() ?: 0
        result = 31 * result + (voiceAccountServerName?.hashCode() ?: 0)
        result = 31 * result + agentUUID.hashCode()
        result = 31 * result + (userName?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        return result
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("voiceSipUriHostname", voiceSipUriHostname)
        bundle.putString("voiceAccountServerName", voiceAccountServerName)
        bundle.putString("agentUUID", agentUUID.toString())
        bundle.putString("userName", userName)
        bundle.putString("password", password)
        return bundle
    }
}