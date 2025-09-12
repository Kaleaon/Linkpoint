package com.lumiyaviewer.lumiya.voice.common.messages;

import android.net.Uri;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.BuildConfig;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceRinging implements VoicePluginMessage {
    @Nullable
    public final UUID agentUUID;
    @Nonnull
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceRinging(Uri uri) {
        UUID uuid = null;
        this.sessionHandle = uri.getQueryParameter("sessionHandle");
        String queryParameter = uri.getQueryParameter("agentUUID");
        this.agentUUID = queryParameter != null ? UUID.fromString(queryParameter) : uuid;
        this.voiceChannelInfo = new VoiceChannelInfo(uri);
    }

    public VoiceRinging(Bundle bundle) {
        UUID uuid = null;
        this.sessionHandle = bundle.getString("sessionHandle");
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        String string = bundle.getString("agentUUID");
        this.agentUUID = string != null ? UUID.fromString(string) : uuid;
    }

    public VoiceRinging(@Nonnull String str, VoiceChannelInfo voiceChannelInfo2, @Nullable UUID uuid) {
        this.sessionHandle = str;
        this.voiceChannelInfo = voiceChannelInfo2;
        this.agentUUID = uuid;
    }

    public Bundle toBundle() {
        String str = null;
        Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", this.sessionHandle);
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        if (this.agentUUID != null) {
            str = this.agentUUID.toString();
        }
        bundle.putString("agentUUID", str);
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
