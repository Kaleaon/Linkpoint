// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.net.Uri$Builder;
import android.os.Bundle;
import android.net.Uri;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceRinging implements VoicePluginMessage
{
    @Nullable
    public final UUID agentUUID;
    @Nonnull
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;
    
    public VoiceRinging(final Uri uri) {
        UUID fromString = null;
        this.sessionHandle = uri.getQueryParameter("sessionHandle");
        final String queryParameter = uri.getQueryParameter("agentUUID");
        if (queryParameter != null) {
            fromString = UUID.fromString(queryParameter);
        }
        this.agentUUID = fromString;
        this.voiceChannelInfo = new VoiceChannelInfo(uri);
    }
    
    public VoiceRinging(final Bundle bundle) {
        final UUID uuid = null;
        this.sessionHandle = bundle.getString("sessionHandle");
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        final String string = bundle.getString("agentUUID");
        UUID fromString;
        if (string == null) {
            fromString = uuid;
        }
        else {
            fromString = UUID.fromString(string);
        }
        this.agentUUID = fromString;
    }
    
    public VoiceRinging(@Nonnull final String sessionHandle, final VoiceChannelInfo voiceChannelInfo, @Nullable final UUID agentUUID) {
        this.sessionHandle = sessionHandle;
        this.voiceChannelInfo = voiceChannelInfo;
        this.agentUUID = agentUUID;
    }
    
    @Override
    public Bundle toBundle() {
        String string = null;
        final Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", this.sessionHandle);
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        if (this.agentUUID != null) {
            string = this.agentUUID.toString();
        }
        bundle.putString("agentUUID", string);
        return bundle;
    }
    
    public Uri toUri() {
        final Uri$Builder appendQueryParameter = new Uri$Builder().scheme("com.lumiyaviewer.lumiya").authority("voice").appendQueryParameter("sessionHandle", this.sessionHandle);
        if (this.agentUUID != null) {
            appendQueryParameter.appendQueryParameter("agentUUID", this.agentUUID.toString());
        }
        this.voiceChannelInfo.appendToUri(appendQueryParameter);
        return appendQueryParameter.build();
    }
}
