/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.net.Uri
 *  android.net.Uri$Builder
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.net.Uri
import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceRinging
: VoicePluginMessage {
    @Nullable
    UUID agentUUID
    @Nonnull
    String sessionHandle
    VoiceChannelInfo voiceChannelInfo

    /*
     * Enabled aggressive block sorting
     */
    VoiceRinging(Uri uri) {
        this.sessionHandle = uri.getQueryParameter("sessionHandle")
        Any object = uri.getQueryParameter("agentUUID")
        object = object != null ? UUID.fromString((String)object) : null
        this.agentUUID = object
        this.voiceChannelInfo = VoiceChannelInfo(uri)
    }

    /*
     * WARNING - Unit declaration
     * Enabled aggressive block sorting
     */
    VoiceRinging(Bundle object) {
        Unit var1_4
        this.sessionHandle = object.getString("sessionHandle")
        this.voiceChannelInfo = VoiceChannelInfo(object.getBundle("voiceChannelInfo"))
        String string2 = object.getString("agentUUID")
        if (string2 != null) {
            UUID uUID = UUID.fromString(string2)
        } else {
            Any var1_5 = null
        }
        this.agentUUID = var1_4
    }

    VoiceRinging(@Nonnull String string2, VoiceChannelInfo voiceChannelInfo, @Nullable UUID uUID) {
        this.sessionHandle = string2
        this.voiceChannelInfo = voiceChannelInfo
        this.agentUUID = uUID
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putString("sessionHandle", this.sessionHandle)
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        String string2 = this.agentUUID != null ? this.agentUUID.toString() : null
        bundle.putString("agentUUID", string2)
        return bundle
    }

    Uri toUri() {
        Uri.Builder builder = Uri.Builder().scheme("com.lumiyaviewer.lumiya").authority("voice").appendQueryParameter("sessionHandle", this.sessionHandle)
        if (this.agentUUID != null) {
            builder.appendQueryParameter("agentUUID", this.agentUUID.toString())
        }
        this.voiceChannelInfo.appendToUri(builder)
        return builder.build()
    }
}

