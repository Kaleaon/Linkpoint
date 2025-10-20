package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice
import javax.annotation.Nullable

class VoiceSetAudioProperties(
    val speakerVolume: Float,
    val speakerVolumeValid: Boolean,
    val audioDevice: VoiceAudioDevice?
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        if (bundle.containsKey("speakerVolume")) bundle.getFloat("speakerVolume") else Float.NaN,
        bundle.containsKey("speakerVolume"),
        if (bundle.containsKey("audioDevice")) {
            try {
                VoiceAudioDevice.valueOf(bundle.getString("audioDevice") ?: "")
            } catch (e: IllegalArgumentException) {
                null
            }
        } else {
            null
        }
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        if (speakerVolumeValid) {
            bundle.putFloat("speakerVolume", speakerVolume)
        }
        audioDevice?.let {
            bundle.putString("audioDevice", it.name)
        }
        return bundle
    }
}