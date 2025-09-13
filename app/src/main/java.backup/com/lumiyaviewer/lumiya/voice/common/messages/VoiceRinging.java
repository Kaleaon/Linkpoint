/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.net.Uri
 *  android.net.Uri$Builder
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.net.Uri;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceRinging
implements VoicePluginMessage {
    @Nullable
    public final UUID agentUUID;
    @Nonnull
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;

    /*
     * Enabled aggressive block sorting
     */
    public VoiceRinging(Uri uri) {
        this.sessionHandle = uri.getQueryParameter("sessionHandle");
        Object object = uri.getQueryParameter("agentUUID");
        object = object != null ? UUID.fromString((String)object) : null;
        this.agentUUID = object;
        this.voiceChannelInfo = new VoiceChannelInfo(uri);
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public VoiceRinging(Bundle object) {
        void var1_4;
        this.sessionHandle = object.getString("sessionHandle");
        this.voiceChannelInfo = new VoiceChannelInfo(object.getBundle("voiceChannelInfo"));
        String string2 = object.getString("agentUUID");
        if (string2 != null) {
            UUID uUID = UUID.fromString(string2);
        } else {
            Object var1_5 = null;
        }
        this.agentUUID = var1_4;
    }

    public VoiceRinging(@Nonnull String string2, VoiceChannelInfo voiceChannelInfo, @Nullable UUID uUID) {
        this.sessionHandle = string2;
        this.voiceChannelInfo = voiceChannelInfo;
        this.agentUUID = uUID;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", this.sessionHandle);
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        String string2 = this.agentUUID != null ? this.agentUUID.toString() : null;
        bundle.putString("agentUUID", string2);
        return bundle;
    }

    public Uri toUri() {
        Uri.Builder builder = new Uri.Builder().scheme("com.lumiyaviewer.lumiya").authority("voice").appendQueryParameter("sessionHandle", this.sessionHandle);
        if (this.agentUUID != null) {
            builder.appendQueryParameter("agentUUID", this.agentUUID.toString());
        }
        this.voiceChannelInfo.appendToUri(builder);
        return builder.build();
    }
}

