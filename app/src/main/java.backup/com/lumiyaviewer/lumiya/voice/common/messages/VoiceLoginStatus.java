/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import javax.annotation.Nullable;

public class VoiceLoginStatus
implements VoicePluginMessage {
    @Nullable
    public final String errorMessage;
    public final boolean loggedIn;
    @Nullable
    public final VoiceLoginInfo voiceLoginInfo;

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public VoiceLoginStatus(Bundle bundle) {
        void var2_4;
        Bundle bundle2 = bundle.getBundle("voiceLoginInfo");
        if (bundle2 != null) {
            VoiceLoginInfo voiceLoginInfo = new VoiceLoginInfo(bundle2);
        } else {
            Object var2_5 = null;
        }
        this.voiceLoginInfo = var2_4;
        this.loggedIn = bundle.getBoolean("loggedIn");
        this.errorMessage = bundle.getString("errorMessage");
    }

    public VoiceLoginStatus(@Nullable VoiceLoginInfo voiceLoginInfo, boolean bl, @Nullable String string2) {
        this.voiceLoginInfo = voiceLoginInfo;
        this.loggedIn = bl;
        this.errorMessage = string2;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        Bundle bundle2 = this.voiceLoginInfo != null ? this.voiceLoginInfo.toBundle() : null;
        bundle.putBundle("voiceLoginInfo", bundle2);
        bundle.putBoolean("loggedIn", this.loggedIn);
        bundle.putString("errorMessage", this.errorMessage);
        return bundle;
    }
}

