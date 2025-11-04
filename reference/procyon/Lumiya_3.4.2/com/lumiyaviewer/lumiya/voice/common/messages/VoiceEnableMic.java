// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceEnableMic implements VoicePluginMessage
{
    public final boolean enableMic;
    
    public VoiceEnableMic(final Bundle bundle) {
        this.enableMic = bundle.getBoolean("enableMic");
    }
    
    public VoiceEnableMic(final boolean enableMic) {
        this.enableMic = enableMic;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBoolean("enableMic", this.enableMic);
        return bundle;
    }
}
