// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceLoginStatus implements VoicePluginMessage
{
    @Nullable
    public final String errorMessage;
    public final boolean loggedIn;
    @Nullable
    public final VoiceLoginInfo voiceLoginInfo;
    
    public VoiceLoginStatus(final Bundle bundle) {
        VoiceLoginInfo voiceLoginInfo = null;
        final Bundle bundle2 = bundle.getBundle("voiceLoginInfo");
        if (bundle2 != null) {
            voiceLoginInfo = new VoiceLoginInfo(bundle2);
        }
        this.voiceLoginInfo = voiceLoginInfo;
        this.loggedIn = bundle.getBoolean("loggedIn");
        this.errorMessage = bundle.getString("errorMessage");
    }
    
    public VoiceLoginStatus(@Nullable final VoiceLoginInfo voiceLoginInfo, final boolean loggedIn, @Nullable final String errorMessage) {
        this.voiceLoginInfo = voiceLoginInfo;
        this.loggedIn = loggedIn;
        this.errorMessage = errorMessage;
    }
    
    @Override
    public Bundle toBundle() {
        Bundle bundle = null;
        final Bundle bundle2 = new Bundle();
        if (this.voiceLoginInfo != null) {
            bundle = this.voiceLoginInfo.toBundle();
        }
        bundle2.putBundle("voiceLoginInfo", bundle);
        bundle2.putBoolean("loggedIn", this.loggedIn);
        bundle2.putString("errorMessage", this.errorMessage);
        return bundle2;
    }
}
