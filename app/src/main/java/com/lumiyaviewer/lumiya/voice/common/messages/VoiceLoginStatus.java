package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import javax.annotation.Nullable;

public class VoiceLoginStatus implements VoicePluginMessage {
    @Nullable
    public final String errorMessage;
    public final boolean loggedIn;
    @Nullable
    public final VoiceLoginInfo voiceLoginInfo;

    public VoiceLoginStatus(Bundle bundle) {
        VoiceLoginInfo voiceLoginInfo2 = null;
        Bundle bundle2 = bundle.getBundle("voiceLoginInfo");
        this.voiceLoginInfo = bundle2 != null ? new VoiceLoginInfo(bundle2) : voiceLoginInfo2;
        this.loggedIn = bundle.getBoolean("loggedIn");
        this.errorMessage = bundle.getString("errorMessage");
    }

    public VoiceLoginStatus(@Nullable VoiceLoginInfo voiceLoginInfo2, boolean z, @Nullable String str) {
        this.voiceLoginInfo = voiceLoginInfo2;
        this.loggedIn = z;
        this.errorMessage = str;
    }

    public Bundle toBundle() {
        Bundle bundle = null;
        Bundle bundle2 = new Bundle();
        if (this.voiceLoginInfo != null) {
            bundle = this.voiceLoginInfo.toBundle();
        }
        bundle2.putBundle("voiceLoginInfo", bundle);
        bundle2.putBoolean("loggedIn", this.loggedIn);
        bundle2.putString("errorMessage", this.errorMessage);
        return bundle2;
    }
}
