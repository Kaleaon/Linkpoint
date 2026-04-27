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
import javax.annotation.Nonnull;

public class VoiceLogin
implements VoicePluginMessage {
    @Nonnull
    public final VoiceLoginInfo voiceLoginInfo;

    public VoiceLogin(Bundle bundle) {
        this.voiceLoginInfo = new VoiceLoginInfo(bundle.getBundle("voiceLoginInfo"));
    }

    public VoiceLogin(@Nonnull VoiceLoginInfo voiceLoginInfo) {
        this.voiceLoginInfo = voiceLoginInfo;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceLoginInfo", this.voiceLoginInfo.toBundle());
        return bundle;
    }
}

