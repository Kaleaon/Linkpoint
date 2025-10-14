/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo
import javax.annotation.Nullable

class VoiceLoginStatus
: VoicePluginMessage {
    @Nullable
    String errorMessage
    Boolean loggedIn
    @Nullable
    VoiceLoginInfo voiceLoginInfo

    /*
     * WARNING - Unit declaration
     * Enabled aggressive block sorting
     */
    VoiceLoginStatus(Bundle bundle) {
        Unit var2_4
        Bundle bundle2 = bundle.getBundle("voiceLoginInfo")
        if (bundle2 != null) {
            VoiceLoginInfo voiceLoginInfo = VoiceLoginInfo(bundle2)
        } else {
            Any var2_5 = null
        }
        this.voiceLoginInfo = var2_4
        this.loggedIn = bundle.getBoolean("loggedIn")
        this.errorMessage = bundle.getString("errorMessage")
    }

    VoiceLoginStatus(@Nullable VoiceLoginInfo voiceLoginInfo, Boolean bl, @Nullable String string2) {
        this.voiceLoginInfo = voiceLoginInfo
        this.loggedIn = bl
        this.errorMessage = string2
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        Bundle bundle2 = this.voiceLoginInfo != null ? this.voiceLoginInfo.toBundle() : null
        bundle.putBundle("voiceLoginInfo", bundle2)
        bundle.putBoolean("loggedIn", this.loggedIn)
        bundle.putString("errorMessage", this.errorMessage)
        return bundle
    }
}

