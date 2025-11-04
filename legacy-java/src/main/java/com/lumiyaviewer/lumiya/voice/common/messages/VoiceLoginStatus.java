package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class VoiceLoginStatus implements VoicePluginMessage {

    @Nullable
    public final String errorMessage;
    public final boolean loggedIn;

    @Nullable
    public final VoiceLoginInfo voiceLoginInfo;

    public VoiceLoginStatus(Bundle bundle) {
        Bundle bundle2 = bundle.getBundle("voiceLoginInfo");
        this.voiceLoginInfo = bundle2 != null ? new VoiceLoginInfo(bundle2) : null;
        this.loggedIn = bundle.getBoolean("loggedIn");
        this.errorMessage = bundle.getString("errorMessage");
    }

    public VoiceLoginStatus(@Nullable VoiceLoginInfo voiceLoginInfo, boolean z, @Nullable String str) {
        this.voiceLoginInfo = voiceLoginInfo;
        this.loggedIn = z;
        this.errorMessage = str;
    }

    @Override // com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceLoginInfo", this.voiceLoginInfo != null ? this.voiceLoginInfo.toBundle() : null);
        bundle.putBoolean("loggedIn", this.loggedIn);
        bundle.putString("errorMessage", this.errorMessage);
        return bundle;
    }
}
