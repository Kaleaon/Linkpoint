package com.lumiyaviewer.lumiya.voice.common.messages;

import android.net.Uri;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.BuildConfig;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class VoiceRinging implements VoicePluginMessage {

    @Nullable
    public final UUID agentUUID;

    @Nonnull
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceRinging(Uri uri) {
        this.sessionHandle = uri.getQueryParameter("sessionHandle");
        String queryParameter = uri.getQueryParameter("agentUUID");
        this.agentUUID = queryParameter != null ? UUID.fromString(queryParameter) : null;
        this.voiceChannelInfo = new VoiceChannelInfo(uri);
    }

    public VoiceRinging(Bundle bundle) {
        this.sessionHandle = bundle.getString("sessionHandle");
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        String string = bundle.getString("agentUUID");
        this.agentUUID = string != null ? UUID.fromString(string) : null;
    }

    public VoiceRinging(@Nonnull String str, VoiceChannelInfo voiceChannelInfo, @Nullable UUID uuid) {
        this.sessionHandle = str;
        this.voiceChannelInfo = voiceChannelInfo;
        this.agentUUID = uuid;
    }

    @Override // com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", this.sessionHandle);
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        bundle.putString("agentUUID", this.agentUUID != null ? this.agentUUID.toString() : null);
        return bundle;
    }

    public Uri toUri() {
        Uri.Builder appendQueryParameter = new Uri.Builder().scheme(BuildConfig.APPLICATION_ID).authority("voice").appendQueryParameter("sessionHandle", this.sessionHandle);
        if (this.agentUUID != null) {
            appendQueryParameter.appendQueryParameter("agentUUID", this.agentUUID.toString());
        }
        this.voiceChannelInfo.appendToUri(appendQueryParameter);
        return appendQueryParameter.build();
    }
}
