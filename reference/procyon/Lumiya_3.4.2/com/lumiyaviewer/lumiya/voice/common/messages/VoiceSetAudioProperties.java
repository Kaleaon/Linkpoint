// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceSetAudioProperties implements VoicePluginMessage
{
    @Nullable
    public final VoiceAudioDevice audioDevice;
    public final float speakerVolume;
    public final boolean speakerVolumeValid;
    
    public VoiceSetAudioProperties(final float speakerVolume, final boolean speakerVolumeValid, @Nullable final VoiceAudioDevice audioDevice) {
        this.speakerVolume = speakerVolume;
        this.speakerVolumeValid = speakerVolumeValid;
        this.audioDevice = audioDevice;
    }
    
    public VoiceSetAudioProperties(final Bundle bundle) {
        float float1;
        if (!(this.speakerVolumeValid = bundle.containsKey("speakerVolume"))) {
            float1 = Float.NaN;
        }
        else {
            float1 = bundle.getFloat("speakerVolume");
        }
        this.speakerVolume = float1;
        VoiceAudioDevice value;
        if (!bundle.containsKey("audioDevice")) {
            value = null;
        }
        else {
            try {
                value = VoiceAudioDevice.valueOf(bundle.getString("audioDevice"));
            }
            catch (final IllegalArgumentException ex) {
                value = null;
            }
        }
        this.audioDevice = value;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        if (this.speakerVolumeValid) {
            bundle.putFloat("speakerVolume", this.speakerVolume);
        }
        if (this.audioDevice != null) {
            bundle.putString("audioDevice", this.audioDevice.name());
        }
        return bundle;
    }
}
