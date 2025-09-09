package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import javax.annotation.Nullable;

public class VoiceInitializeReply implements VoicePluginMessage {
    public final boolean appVersionOk;
    @Nullable
    public final String errorMessage;
    public final int pluginVersionCode;

    public VoiceInitializeReply(int i, @Nullable String str, boolean z) {
        this.pluginVersionCode = i;
        this.errorMessage = str;
        this.appVersionOk = z;
    }

    public VoiceInitializeReply(Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode");
        this.errorMessage = bundle.getString("errorMessage");
        this.appVersionOk = bundle.getBoolean("appVersionOk");
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", this.pluginVersionCode);
        bundle.putString("errorMessage", this.errorMessage);
        bundle.putBoolean("appVersionOk", this.appVersionOk);
        return bundle;
    }
}
