package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceLogout implements VoicePluginMessage {
    public Bundle toBundle() {
        return new Bundle();
    }
}
