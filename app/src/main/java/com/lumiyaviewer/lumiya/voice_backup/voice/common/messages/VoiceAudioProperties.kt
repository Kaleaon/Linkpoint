package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState
import javax.annotation.Nonnull

class VoiceAudioProperties(
    val speakerVolume: Float,
    val speakerphoneOn: Boolean,
    val bluetoothState: VoiceBluetoothState
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        bundle.getFloat("speakerVolume"),
        bundle.getBoolean("speakerphoneOn"),
        try {
            VoiceBluetoothState.valueOf(bundle.getString("bluetoothState") ?: "Error")
        } catch (e: IllegalArgumentException) {
            VoiceBluetoothState.Error
        }
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putFloat("speakerVolume", speakerVolume)
        bundle.putBoolean("speakerphoneOn", speakerphoneOn)
        bundle.putString("bluetoothState", bluetoothState.name)
        return bundle
    }
}