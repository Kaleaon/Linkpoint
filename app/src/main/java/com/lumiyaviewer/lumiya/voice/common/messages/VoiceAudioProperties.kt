/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState
import javax.annotation.Nonnull

class VoiceAudioProperties
: VoicePluginMessage {
    @Nonnull
    VoiceBluetoothState bluetoothState
    Float speakerVolume
    Boolean speakerphoneOn

    VoiceAudioProperties(Float f, Boolean bl, @Nonnull VoiceBluetoothState voiceBluetoothState) {
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
    VoiceAudioProperties(Bundle object) {
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

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putFloat("speakerVolume", this.speakerVolume)
        bundle.putBoolean("speakerphoneOn", this.speakerphoneOn)
        bundle.putString("bluetoothState", this.bluetoothState.name())
        return bundle
    }
}

