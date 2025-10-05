package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import javax.annotation.Nullable

class VoiceInitializeReply : VoicePluginMessage {
    val Boolean appVersionOk
    val String errorMessage
    val Int pluginVersionCode

    public VoiceInitializeReply(Int n, String string2, Boolean bl) {
        this.pluginVersionCode = n
        this.errorMessage = string2
        this.appVersionOk = bl
    }

    public VoiceInitializeReply(Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode")
        this.errorMessage = bundle.getString("errorMessage")
        this.appVersionOk = bundle.getBoolean("appVersionOk")
    }

    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putInt("pluginVersionCode", this.pluginVersionCode)
        bundle.putString("errorMessage", this.errorMessage)
        bundle.putBoolean("appVersionOk", this.appVersionOk)
        return bundle
    }
}

