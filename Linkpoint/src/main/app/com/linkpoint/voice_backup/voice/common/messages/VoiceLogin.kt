/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceLoginInfo
import javax.annotation.Nonnull

class VoiceLogin
: VoicePluginMessage {
    @Nonnull
    VoiceLoginInfo voiceLoginInfo

    VoiceLogin(Bundle bundle) {
        this.voiceLoginInfo = VoiceLoginInfo(bundle.getBundle("voiceLoginInfo"))
    }

    VoiceLogin(@Nonnull VoiceLoginInfo voiceLoginInfo) {
        this.voiceLoginInfo = voiceLoginInfo
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("voiceLoginInfo", this.voiceLoginInfo.toBundle())
        return bundle
    }
}

