package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceLoginInfo
import javax.annotation.Nullable

class VoiceLoginStatus : VoicePluginMessage {
    val String errorMessage
    val Boolean loggedIn
    val VoiceLoginInfo voiceLoginInfo

    /*
     * WARNING - Unit declaration
     * Enabled aggressive block sorting
     */
    public VoiceLoginStatus(Bundle bundle) {
        Unit var2_4
        Bundle bundle2 = bundle.getBundle("voiceLoginInfo")
        if (bundle2 != null) {
            VoiceLoginInfo voiceLoginInfo = VoiceLoginInfo(bundle2)
        } else {
            Object var2_5 = null
        }
        this.voiceLoginInfo = var2_4
        this.loggedIn = bundle.getBoolean("loggedIn")
        this.errorMessage = bundle.getString("errorMessage")
    }

    public VoiceLoginStatus(VoiceLoginInfo voiceLoginInfo, Boolean bl, String string2) {
        this.voiceLoginInfo = voiceLoginInfo
        this.loggedIn = bl
        this.errorMessage = string2
    }

    /*
     * Enabled aggressive block sorting
     */
    override Bundle toBundle() {
        Bundle bundle = Bundle()
        Bundle bundle2 = this.voiceLoginInfo != null ? this.voiceLoginInfo.toBundle() : null
        bundle.putBundle("voiceLoginInfo", bundle2)
        bundle.putBoolean("loggedIn", this.loggedIn)
        bundle.putString("errorMessage", this.errorMessage)
        return bundle
    }
}

