/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceAudioDevice;
import javax.annotation.Nullable;

public class VoiceSetAudioProperties
implements VoicePluginMessage {
    @Nullable
    public final VoiceAudioDevice audioDevice;
    public final float speakerVolume;
    public final boolean speakerVolumeValid;

    public VoiceSetAudioProperties(float f, boolean bl, @Nullable VoiceAudioDevice voiceAudioDevice) {
        this.speakerVolume = f;
        this.speakerVolumeValid = bl;
        this.audioDevice = voiceAudioDevice;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public VoiceSetAudioProperties(Bundle bundle) {
        this.speakerVolumeValid = bundle.containsKey("speakerVolume");
        float f = this.speakerVolumeValid ? bundle.getFloat("speakerVolume") : Float.NaN;
        this.speakerVolume = f;
        VoiceAudioDevice voiceAudioDevice = null;
        if (bundle.containsKey("audioDevice")) {
            try {
                voiceAudioDevice = VoiceAudioDevice.valueOf(bundle.getString("audioDevice"));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                voiceAudioDevice = null;
            }
        }
        this.audioDevice = voiceAudioDevice;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        if (this.speakerVolumeValid) {
            bundle.putFloat("speakerVolume", this.speakerVolume);
        }
        if (this.audioDevice != null) {
            bundle.putString("audioDevice", this.audioDevice.name());
        }
        return bundle;
    }
}

