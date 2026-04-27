/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import javax.annotation.Nullable;

public class VoiceInitializeReply
implements VoicePluginMessage {
    public final boolean appVersionOk;
    @Nullable
    public final String errorMessage;
    public final int pluginVersionCode;

    public VoiceInitializeReply(int n, @Nullable String string2, boolean bl) {
        this.pluginVersionCode = n;
        this.errorMessage = string2;
        this.appVersionOk = bl;
    }

    public VoiceInitializeReply(Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode");
        this.errorMessage = bundle.getString("errorMessage");
        this.appVersionOk = bundle.getBoolean("appVersionOk");
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", this.pluginVersionCode);
        bundle.putString("errorMessage", this.errorMessage);
        bundle.putBoolean("appVersionOk", this.appVersionOk);
        return bundle;
    }
}

