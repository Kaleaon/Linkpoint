package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceBluetoothState
import javax.annotation.Nonnull

class VoiceAudioProperties : VoicePluginMessage {
    val VoiceBluetoothState bluetoothState
    val Float speakerVolume
    val Boolean speakerphoneOn

    public VoiceAudioProperties(Float f, Boolean bl, VoiceBluetoothState voiceBluetoothState) {
        this.speakerVolume = f
        this.speakerphoneOn = bl
        this.bluetoothState = voiceBluetoothState
    }

    /*
     * WARNING - Unit declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public VoiceAudioProperties(Bundle object) {
        Unit var1_3
        this.speakerVolume = object.getFloat("speakerVolume")
        this.speakerphoneOn = object.getBoolean("speakerphoneOn")
        try {
            VoiceBluetoothState voiceBluetoothState = VoiceBluetoothState.valueOf(object.getString("bluetoothState"))
        }
        catch (IllegalArgumentException illegalArgumentException) {
            VoiceBluetoothState voiceBluetoothState = VoiceBluetoothState.Error
        }
        this.bluetoothState = var1_3
    }

    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putFloat("speakerVolume", this.speakerVolume)
        bundle.putBoolean("speakerphoneOn", this.speakerphoneOn)
        bundle.putString("bluetoothState", this.bluetoothState.name())
        return bundle
    }
}

