/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage

class VoiceInitialize
: VoicePluginMessage {
    Int appVersionCode

    VoiceInitialize(Int n) {
        this.appVersionCode = n
    }

    VoiceInitialize(Bundle bundle) {
        this.appVersionCode = bundle.getInt("appVersionCode")
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putInt("appVersionCode", this.appVersionCode)
        return bundle
    }
}

