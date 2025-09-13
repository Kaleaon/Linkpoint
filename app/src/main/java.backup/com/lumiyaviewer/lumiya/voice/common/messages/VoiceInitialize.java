/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceInitialize
implements VoicePluginMessage {
    public final int appVersionCode;

    public VoiceInitialize(int n) {
        this.appVersionCode = n;
    }

    public VoiceInitialize(Bundle bundle) {
        this.appVersionCode = bundle.getInt("appVersionCode");
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("appVersionCode", this.appVersionCode);
        return bundle;
    }
}

