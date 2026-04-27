/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.net.Uri
 *  android.net.Uri$Builder
 *  android.os.Bundle
 */
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

    public VoiceChannelInfo(String string2, boolean bl, boolean bl2) {
        this.voiceChannelURI = string2;
        this.isSpatial = bl;
        this.isConference = bl2;
    }

    public VoiceChannelInfo(@Nonnull UUID object, @Nonnull String string2) {
        object = Bytes.concat(Longs.toByteArray(((UUID)object).getMostSignificantBits()), Longs.toByteArray(((UUID)object).getLeastSignificantBits()));
        object = "x" + Base64.encodeToString((byte[])object, false).replace('+', '-').replace('/', '_');
        this.voiceChannelURI = "sip:" + (String)object + "@" + string2;
        this.isSpatial = false;
        this.isConference = false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Nullable
    public static UUID agentUUIDFromURI(String object) {
        Object object2 = Strings.nullToEmpty((String)object);
        int n = ((String)object2).indexOf(58);
        object = object2;
        if (n != -1) {
            object = ((String)object2).substring(n + 1);
        }
        n = ((String)object).indexOf(64);
        object2 = object;
        if (n != -1) {
            object2 = ((String)object).substring(0, n);
        }
        object = object2;
        if (((String)object2).startsWith("x")) {
            object = ((String)object2).substring(1);
        }
        if ((object = (Object)Base64.decode(((String)object).replace("-", "+").replace("_", "/"))) == null) return null;
        if (((Object)object).length != 16) return null;
        return new UUID(Longs.fromByteArray(Arrays.copyOfRange((byte[])object, 0, 8)), Longs.fromByteArray(Arrays.copyOfRange((byte[])object, 8, 16)));
    }

    /*
     * Enabled aggressive block sorting
     */
    public void appendToUri(Uri.Builder builder) {
        builder.appendQueryParameter("voiceChannelURI", this.voiceChannelURI);
        String string2 = this.isSpatial ? "true" : "false";
        builder.appendQueryParameter("isSpatial", string2);
        string2 = this.isConference ? "true" : "false";
        builder.appendQueryParameter("isConference", string2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        boolean bl = true;
        boolean bl2 = false;
        if (this == object) {
            return true;
        }
        boolean bl3 = bl2;
        if (object == null) return bl3;
        bl3 = bl2;
        if (this.getClass() != object.getClass()) return bl3;
        object = (VoiceChannelInfo)object;
        bl3 = bl2;
        if (this.isSpatial != ((VoiceChannelInfo)object).isSpatial) return bl3;
        bl3 = bl2;
        if (this.isConference != ((VoiceChannelInfo)object).isConference) return bl3;
        if (this.voiceChannelURI != null) {
            return this.voiceChannelURI.equals(((VoiceChannelInfo)object).voiceChannelURI);
        }
        bl3 = bl;
        if (((VoiceChannelInfo)object).voiceChannelURI == null) return bl3;
        return false;
    }

    @Nullable
    public UUID getAgentUUID() {
        return VoiceChannelInfo.agentUUIDFromURI(this.voiceChannelURI);
    }

    /*
     * Enabled aggressive block sorting
     */
    public int hashCode() {
        int n = 1;
        int n2 = this.voiceChannelURI != null ? this.voiceChannelURI.hashCode() : 0;
        int n3 = this.isSpatial ? 1 : 0;
        if (this.isConference) {
            return (n2 * 31 + n3) * 31 + n;
        }
        n = 0;
        return (n2 * 31 + n3) * 31 + n;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("voiceChannelURI", this.voiceChannelURI);
        bundle.putBoolean("isSpatial", this.isSpatial);
        bundle.putBoolean("isConference", this.isConference);
        return bundle;
    }
}

