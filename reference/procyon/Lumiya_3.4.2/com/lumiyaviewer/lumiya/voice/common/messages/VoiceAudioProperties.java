// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceAudioProperties implements VoicePluginMessage
{
    @Nonnull
    public final VoiceBluetoothState bluetoothState;
    public final float speakerVolume;
    public final boolean speakerphoneOn;
    
    public VoiceAudioProperties(final float speakerVolume, final boolean speakerphoneOn, @Nonnull final VoiceBluetoothState bluetoothState) {
        this.speakerVolume = speakerVolume;
        this.speakerphoneOn = speakerphoneOn;
        this.bluetoothState = bluetoothState;
    }
    
    public VoiceAudioProperties(final Bundle bundle) {
        this.speakerVolume = bundle.getFloat("speakerVolume");
        this.speakerphoneOn = bundle.getBoolean("speakerphoneOn");
        while (true) {
            try {
                final VoiceBluetoothState bluetoothState = VoiceBluetoothState.valueOf(bundle.getString("bluetoothState"));
                this.bluetoothState = bluetoothState;
            }
            catch (final IllegalArgumentException ex) {
                final VoiceBluetoothState bluetoothState = VoiceBluetoothState.Error;
                continue;
            }
            break;
        }
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putFloat("speakerVolume", this.speakerVolume);
        bundle.putBoolean("speakerphoneOn", this.speakerphoneOn);
        bundle.putString("bluetoothState", this.bluetoothState.name());
        return bundle;
    }
}
