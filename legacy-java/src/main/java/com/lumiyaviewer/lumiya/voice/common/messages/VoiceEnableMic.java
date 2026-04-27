package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

/* loaded from: classes.dex */
public class VoiceEnableMic implements VoicePluginMessage {
    public final boolean enableMic;

    public VoiceEnableMic(Bundle bundle) {
        this.enableMic = bundle.getBoolean("enableMic");
    }

    public VoiceEnableMic(boolean z) {
        this.enableMic = z;
    }

    @Override // com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("enableMic", this.enableMic);
        return bundle;
    }
}
