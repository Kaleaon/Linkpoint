/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice
import javax.annotation.Nullable

class VoiceSetAudioProperties
: VoicePluginMessage {
    @Nullable
    VoiceAudioDevice audioDevice
    Float speakerVolume
    Boolean speakerVolumeValid

    VoiceSetAudioProperties(Float f, Boolean bl, @Nullable VoiceAudioDevice voiceAudioDevice) {
        this.speakerVolume = f
        this.speakerVolumeValid = bl
        this.audioDevice = voiceAudioDevice
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    VoiceSetAudioProperties(Bundle bundle) {
        this.speakerVolumeValid = bundle.containsKey("speakerVolume")
        Float f = this.speakerVolumeValid ? bundle.getFloat("speakerVolume") : Float.NaN
        this.speakerVolume = f
        VoiceAudioDevice voiceAudioDevice = null
        if (bundle.containsKey("audioDevice")) {
            try {
                voiceAudioDevice = VoiceAudioDevice.valueOf(bundle.getString("audioDevice"))
            }
            catch (IllegalArgumentException illegalArgumentException) {
                voiceAudioDevice = null
            }
        }
        this.audioDevice = voiceAudioDevice
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        if (this.speakerVolumeValid) {
            bundle.putFloat("speakerVolume", this.speakerVolume)
        }
        if (this.audioDevice != null) {
            bundle.putString("audioDevice", this.audioDevice.name())
        }
        return bundle
    }
}

