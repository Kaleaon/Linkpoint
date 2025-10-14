/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage

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

