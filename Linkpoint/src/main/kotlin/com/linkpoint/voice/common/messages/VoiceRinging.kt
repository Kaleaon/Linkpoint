package com.linkpoint.voice.common.messages

import android.net.Uri
import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceRinging : VoicePluginMessage {
    val UUID agentUUID
    val String sessionHandle
    val VoiceChannelInfo voiceChannelInfo

    /*
     * Enabled aggressive block sorting
     */
    public VoiceRinging(Uri uri) {
        this.sessionHandle = uri.getQueryParameter("sessionHandle")
        Object object = uri.getQueryParameter("agentUUID")
        object = object != null ? UUID.fromString((String)object) : null
        this.agentUUID = object
        this.voiceChannelInfo = VoiceChannelInfo(uri)
    }

    /*
     * WARNING - Unit declaration
     * Enabled aggressive block sorting
     */
    public VoiceRinging(Bundle object) {
        Unit var1_4
        this.sessionHandle = object.getString("sessionHandle")
        this.voiceChannelInfo = VoiceChannelInfo(object.getBundle("voiceChannelInfo"))
        String string2 = object.getString("agentUUID")
        if (string2 != null) {
            UUID uUID = UUID.fromString(string2)
        } else {
            Object var1_5 = null
        }
        this.agentUUID = var1_4
    }

    public VoiceRinging(String string2, VoiceChannelInfo voiceChannelInfo, UUID uUID) {
        this.sessionHandle = string2
        this.voiceChannelInfo = voiceChannelInfo
        this.agentUUID = uUID
    }

    /*
     * Enabled aggressive block sorting
     */
    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putString("sessionHandle", this.sessionHandle)
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        String string2 = this.agentUUID != null ? this.agentUUID.toString() : null
        bundle.putString("agentUUID", string2)
        return bundle
    }

    public Uri toUri() {
        Uri.Builder builder = Uri.Builder().scheme("com.linkpoint").authority("voice").appendQueryParameter("sessionHandle", this.sessionHandle)
        if (this.agentUUID != null) {
            builder.appendQueryParameter("agentUUID", this.agentUUID.toString())
        }
        this.voiceChannelInfo.appendToUri(builder)
        return builder.build()
    }
}

