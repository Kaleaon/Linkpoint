package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceAudioDevice
import javax.annotation.Nullable

class VoiceSetAudioProperties : VoicePluginMessage {
    val VoiceAudioDevice audioDevice
    val Float speakerVolume
    val Boolean speakerVolumeValid

    public VoiceSetAudioProperties(Float f, Boolean bl, VoiceAudioDevice voiceAudioDevice) {
        this.speakerVolume = f
        this.speakerVolumeValid = bl
        this.audioDevice = voiceAudioDevice
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public VoiceSetAudioProperties(Bundle bundle) {
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

    override Bundle toBundle() {
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

