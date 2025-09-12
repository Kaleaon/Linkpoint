package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import javax.annotation.Nonnull;

public class VoiceAudioProperties implements VoicePluginMessage {
    @Nonnull
    public final VoiceBluetoothState bluetoothState;
    public final float speakerVolume;
    public final boolean speakerphoneOn;

    public VoiceAudioProperties(float f, boolean z, @Nonnull VoiceBluetoothState voiceBluetoothState) {
        this.speakerVolume = f;
        this.speakerphoneOn = z;
        this.bluetoothState = voiceBluetoothState;
    }

    public VoiceAudioProperties(Bundle bundle) {
        VoiceBluetoothState voiceBluetoothState;
        this.speakerVolume = bundle.getFloat("speakerVolume");
        this.speakerphoneOn = bundle.getBoolean("speakerphoneOn");
        try {
            voiceBluetoothState = VoiceBluetoothState.valueOf(bundle.getString("bluetoothState"));
        } catch (IllegalArgumentException e) {
            voiceBluetoothState = VoiceBluetoothState.Error;
        }
        this.bluetoothState = voiceBluetoothState;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putFloat("speakerVolume", this.speakerVolume);
        bundle.putBoolean("speakerphoneOn", this.speakerphoneOn);
        bundle.putString("bluetoothState", this.bluetoothState.name());
        return bundle;
    }
}
