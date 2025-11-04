// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.net.Uri$Builder;
import javax.annotation.Nullable;
import java.util.Arrays;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.base64.Base64;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import javax.annotation.Nonnull;
import java.util.UUID;
import android.os.Bundle;
import com.google.common.base.Objects;
import android.net.Uri;

public class VoiceChannelInfo
{
    public final boolean isConference;
    public final boolean isSpatial;
    public final String voiceChannelURI;
    
    public VoiceChannelInfo(final Uri uri) {
        this.voiceChannelURI = uri.getQueryParameter("voiceChannelURI");
        this.isSpatial = Objects.equal(uri.getQueryParameter("isSpatial"), "true");
        this.isConference = Objects.equal(uri.getQueryParameter("isConference"), "true");
    }
    
    public VoiceChannelInfo(final Bundle bundle) {
        this.voiceChannelURI = bundle.getString("voiceChannelURI");
        this.isSpatial = bundle.getBoolean("isSpatial");
        this.isConference = bundle.getBoolean("isConference");
    }
    
    public VoiceChannelInfo(final String voiceChannelURI, final boolean isSpatial, final boolean isConference) {
        this.voiceChannelURI = voiceChannelURI;
        this.isSpatial = isSpatial;
        this.isConference = isConference;
    }
    
    public VoiceChannelInfo(@Nonnull final UUID uuid, @Nonnull final String str) {
        this.voiceChannelURI = "sip:" + ("x" + Base64.encodeToString(Bytes.concat(new byte[][] { Longs.toByteArray(uuid.getMostSignificantBits()), Longs.toByteArray(uuid.getLeastSignificantBits()) }), false).replace('+', '-').replace('/', '_')) + "@" + str;
        this.isSpatial = false;
        this.isConference = false;
    }
    
    @Nullable
    public static UUID agentUUIDFromURI(String s) {
        s = Strings.nullToEmpty(s);
        final int index = s.indexOf(58);
        if (index != -1) {
            s = s.substring(index + 1);
        }
        final int index2 = s.indexOf(64);
        if (index2 != -1) {
            s = s.substring(0, index2);
        }
        if (s.startsWith("x")) {
            s = s.substring(1);
        }
        final byte[] decode = Base64.decode(s.replace("-", "+").replace("_", "/"));
        if (decode != null && decode.length == 16) {
            return new UUID(Longs.fromByteArray(Arrays.copyOfRange(decode, 0, 8)), Longs.fromByteArray(Arrays.copyOfRange(decode, 8, 16)));
        }
        return null;
    }
    
    public void appendToUri(final Uri$Builder uri$Builder) {
        uri$Builder.appendQueryParameter("voiceChannelURI", this.voiceChannelURI);
        String s;
        if (!this.isSpatial) {
            s = "false";
        }
        else {
            s = "true";
        }
        uri$Builder.appendQueryParameter("isSpatial", s);
        String s2;
        if (!this.isConference) {
            s2 = "false";
        }
        else {
            s2 = "true";
        }
        uri$Builder.appendQueryParameter("isConference", s2);
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final VoiceChannelInfo voiceChannelInfo = (VoiceChannelInfo)o;
        if (this.isSpatial != voiceChannelInfo.isSpatial) {
            return false;
        }
        if (this.isConference == voiceChannelInfo.isConference) {
            if (this.voiceChannelURI == null) {
                if (voiceChannelInfo.voiceChannelURI == null) {
                    equals = true;
                }
            }
            else {
                equals = this.voiceChannelURI.equals(voiceChannelInfo.voiceChannelURI);
            }
            return equals;
        }
        return false;
    }
    
    @Nullable
    public UUID getAgentUUID() {
        return agentUUIDFromURI(this.voiceChannelURI);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        int hashCode;
        if (this.voiceChannelURI == null) {
            hashCode = 0;
        }
        else {
            hashCode = this.voiceChannelURI.hashCode();
        }
        int n2;
        if (!this.isSpatial) {
            n2 = 0;
        }
        else {
            n2 = 1;
        }
        if (this.isConference) {
            n = 1;
        }
        return (n2 + hashCode * 31) * 31 + n;
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("voiceChannelURI", this.voiceChannelURI);
        bundle.putBoolean("isSpatial", this.isSpatial);
        bundle.putBoolean("isConference", this.isConference);
        return bundle;
    }
}
