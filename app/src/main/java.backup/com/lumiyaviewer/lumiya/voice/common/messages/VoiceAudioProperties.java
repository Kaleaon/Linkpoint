/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import javax.annotation.Nonnull;

public class VoiceAudioProperties
implements VoicePluginMessage {
    @Nonnull
    public final VoiceBluetoothState bluetoothState;
    public final float speakerVolume;
    public final boolean speakerphoneOn;

    public VoiceAudioProperties(float f, boolean bl, @Nonnull VoiceBluetoothState voiceBluetoothState) {
        this.speakerVolume = f;
        this.speakerphoneOn = bl;
        this.bluetoothState = voiceBluetoothState;
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public VoiceAudioProperties(Bundle object) {
        void var1_3;
        this.speakerVolume = object.getFloat("speakerVolume");
        this.speakerphoneOn = object.getBoolean("speakerphoneOn");
        try {
            VoiceBluetoothState voiceBluetoothState = VoiceBluetoothState.valueOf(object.getString("bluetoothState"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            VoiceBluetoothState voiceBluetoothState = VoiceBluetoothState.Error;
        }
        this.bluetoothState = var1_3;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putFloat("speakerVolume", this.speakerVolume);
        bundle.putBoolean("speakerphoneOn", this.speakerphoneOn);
        bundle.putString("bluetoothState", this.bluetoothState.name());
        return bundle;
    }
}

