/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage

class VoiceEnableMic
: VoicePluginMessage {
    Boolean enableMic

    VoiceEnableMic(Bundle bundle) {
        this.enableMic = bundle.getBoolean("enableMic")
    }

    VoiceEnableMic(Boolean bl) {
        this.enableMic = bl
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBoolean("enableMic", this.enableMic)
        return bundle
    }
}

