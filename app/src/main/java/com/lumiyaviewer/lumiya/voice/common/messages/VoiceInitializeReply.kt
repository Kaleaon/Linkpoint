/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import javax.annotation.Nullable

class VoiceInitializeReply
: VoicePluginMessage {
    Boolean appVersionOk
    @Nullable
    String errorMessage
    Int pluginVersionCode

    VoiceInitializeReply(Int n, @Nullable String string2, Boolean bl) {
        this.pluginVersionCode = n
        this.errorMessage = string2
        this.appVersionOk = bl
    }

    VoiceInitializeReply(Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode")
        this.errorMessage = bundle.getString("errorMessage")
        this.appVersionOk = bundle.getBoolean("appVersionOk")
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putInt("pluginVersionCode", this.pluginVersionCode)
        bundle.putString("errorMessage", this.errorMessage)
        bundle.putBoolean("appVersionOk", this.appVersionOk)
        return bundle
    }
}

