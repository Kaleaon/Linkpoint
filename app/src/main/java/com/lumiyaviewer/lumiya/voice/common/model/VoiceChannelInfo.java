package com.lumiyaviewer.lumiya.voice.common.model;

import android.net.Uri;
import android.os.Bundle;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.lumiyaviewer.lumiya.base64.Base64;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceChannelInfo {
    public final boolean isConference;
    public final boolean isSpatial;
    public final String voiceChannelURI;

    public VoiceChannelInfo(Uri uri) {
        this.voiceChannelURI = uri.getQueryParameter("voiceChannelURI");
        this.isSpatial = Objects.equal(uri.getQueryParameter("isSpatial"), "true");
        this.isConference = Objects.equal(uri.getQueryParameter("isConference"), "true");
    }

    public VoiceChannelInfo(Bundle bundle) {
        this.voiceChannelURI = bundle.getString("voiceChannelURI");
        this.isSpatial = bundle.getBoolean("isSpatial");
        this.isConference = bundle.getBoolean("isConference");
    }

    public VoiceChannelInfo(String str, boolean z, boolean z2) {
        this.voiceChannelURI = str;
        this.isSpatial = z;
        this.isConference = z2;
    }

    public VoiceChannelInfo(@Nonnull UUID uuid, @Nonnull String str) {
        this.voiceChannelURI = "sip:" + ("x" + Base64.encodeToString(Bytes.concat(Longs.toByteArray(uuid.getMostSignificantBits()), Longs.toByteArray(uuid.getLeastSignificantBits())), false).replace('+', '-').replace('/', '_')) + "@" + str;
        this.isSpatial = false;
        this.isConference = false;
    }

    @Nullable
    public static UUID agentUUIDFromURI(String str) {
        String nullToEmpty = Strings.nullToEmpty(str);
        int indexOf = nullToEmpty.indexOf(58);
        if (indexOf != -1) {
            nullToEmpty = nullToEmpty.substring(indexOf + 1);
        }
        int indexOf2 = nullToEmpty.indexOf(64);
        if (indexOf2 != -1) {
            nullToEmpty = nullToEmpty.substring(0, indexOf2);
        }
        if (nullToEmpty.startsWith("x")) {
            nullToEmpty = nullToEmpty.substring(1);
        }
        byte[] decode = Base64.decode(nullToEmpty.replace("-", "+").replace("_", "/"));
        if (decode != null && decode.length == 16) {
            return new UUID(Longs.fromByteArray(Arrays.copyOfRange(decode, 0, 8)), Longs.fromByteArray(Arrays.copyOfRange(decode, 8, 16)));
        }
        return null;
    }

    public void appendToUri(Uri.Builder builder) {
        builder.appendQueryParameter("voiceChannelURI", this.voiceChannelURI);
        builder.appendQueryParameter("isSpatial", !this.isSpatial ? "false" : "true");
        builder.appendQueryParameter("isConference", !this.isConference ? "false" : "true");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VoiceChannelInfo voiceChannelInfo = (VoiceChannelInfo) obj;
        if (this.isSpatial == voiceChannelInfo.isSpatial && this.isConference == voiceChannelInfo.isConference) {
            return this.voiceChannelURI == null ? voiceChannelInfo.voiceChannelURI == null : this.voiceChannelURI.equals(voiceChannelInfo.voiceChannelURI);
        }
        return false;
    }

    @Nullable
    public UUID getAgentUUID() {
        return agentUUIDFromURI(this.voiceChannelURI);
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((!this.isSpatial ? 0 : 1) + ((this.voiceChannelURI == null ? 0 : this.voiceChannelURI.hashCode()) * 31)) * 31;
        if (this.isConference) {
            i = 1;
        }
        return hashCode + i;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("voiceChannelURI", this.voiceChannelURI);
        bundle.putBoolean("isSpatial", this.isSpatial);
        bundle.putBoolean("isConference", this.isConference);
        return bundle;
    }
}
