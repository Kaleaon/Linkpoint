package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import javax.annotation.Nullable

class VoiceInitializeReply(
    val pluginVersionCode: Int,
    val errorMessage: String?,
    val appVersionOk: Boolean
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        bundle.getInt("pluginVersionCode"),
        bundle.getString("errorMessage"),
        bundle.getBoolean("appVersionOk")
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt("pluginVersionCode", pluginVersionCode)
        bundle.putString("errorMessage", errorMessage)
        bundle.putBoolean("appVersionOk", appVersionOk)
        return bundle
    }
}