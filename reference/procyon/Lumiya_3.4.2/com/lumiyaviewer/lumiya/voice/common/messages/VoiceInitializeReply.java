// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceInitializeReply implements VoicePluginMessage
{
    public final boolean appVersionOk;
    @Nullable
    public final String errorMessage;
    public final int pluginVersionCode;
    
    public VoiceInitializeReply(final int pluginVersionCode, @Nullable final String errorMessage, final boolean appVersionOk) {
        this.pluginVersionCode = pluginVersionCode;
        this.errorMessage = errorMessage;
        this.appVersionOk = appVersionOk;
    }
    
    public VoiceInitializeReply(final Bundle bundle) {
        this.pluginVersionCode = bundle.getInt("pluginVersionCode");
        this.errorMessage = bundle.getString("errorMessage");
        this.appVersionOk = bundle.getBoolean("appVersionOk");
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("pluginVersionCode", this.pluginVersionCode);
        bundle.putString("errorMessage", this.errorMessage);
        bundle.putBoolean("appVersionOk", this.appVersionOk);
        return bundle;
    }
}
