package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceLoginInfo
import javax.annotation.Nullable

class VoiceLoginStatus(
    val voiceLoginInfo: VoiceLoginInfo?,
    val loggedIn: Boolean,
    val errorMessage: String?
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        bundle.getBundle("voiceLoginInfo")?.let { VoiceLoginInfo(it) },
        bundle.getBoolean("loggedIn"),
        bundle.getString("errorMessage")
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putBundle("voiceLoginInfo", voiceLoginInfo?.toBundle())
        bundle.putBoolean("loggedIn", loggedIn)
        bundle.putString("errorMessage", errorMessage)
        return bundle
    }
}