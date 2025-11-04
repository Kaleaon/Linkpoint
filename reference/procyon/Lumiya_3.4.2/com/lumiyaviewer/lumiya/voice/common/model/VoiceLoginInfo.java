// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import javax.annotation.Nonnull;
import java.util.UUID;

public class VoiceLoginInfo
{
    @Nonnull
    public final UUID agentUUID;
    public final String password;
    public final String userName;
    public final String voiceAccountServerName;
    public final String voiceSipUriHostname;
    
    public VoiceLoginInfo(final Bundle bundle) {
        this.voiceSipUriHostname = bundle.getString("voiceSipUriHostname");
        this.voiceAccountServerName = bundle.getString("voiceAccountServerName");
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.userName = bundle.getString("userName");
        this.password = bundle.getString("password");
    }
    
    public VoiceLoginInfo(final String voiceSipUriHostname, final String voiceAccountServerName, @Nonnull final UUID agentUUID, final String userName, final String password) {
        this.voiceSipUriHostname = voiceSipUriHostname;
        this.voiceAccountServerName = voiceAccountServerName;
        this.agentUUID = agentUUID;
        this.userName = userName;
        this.password = password;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (this == o) {
            return true;
        }
        if (o != null && this.getClass() == o.getClass()) {
            final VoiceLoginInfo voiceLoginInfo = (VoiceLoginInfo)o;
            if (this.voiceSipUriHostname == null) {
                if (voiceLoginInfo.voiceSipUriHostname != null) {
                    return false;
                }
            }
            else if (!this.voiceSipUriHostname.equals(voiceLoginInfo.voiceSipUriHostname)) {
                return false;
            }
            if (this.voiceAccountServerName == null) {
                if (voiceLoginInfo.voiceAccountServerName != null) {
                    return false;
                }
            }
            else if (!this.voiceAccountServerName.equals(voiceLoginInfo.voiceAccountServerName)) {
                return false;
            }
            if (this.agentUUID.equals(voiceLoginInfo.agentUUID)) {
                if (this.userName == null) {
                    if (voiceLoginInfo.userName != null) {
                        return false;
                    }
                }
                else if (!this.userName.equals(voiceLoginInfo.userName)) {
                    return false;
                }
                if (this.password == null) {
                    if (voiceLoginInfo.password == null) {
                        equals = true;
                    }
                }
                else {
                    equals = this.password.equals(voiceLoginInfo.password);
                }
                return equals;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int hashCode2;
        if (this.voiceSipUriHostname == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = this.voiceSipUriHostname.hashCode();
        }
        int hashCode3;
        if (this.voiceAccountServerName == null) {
            hashCode3 = 0;
        }
        else {
            hashCode3 = this.voiceAccountServerName.hashCode();
        }
        final int hashCode4 = this.agentUUID.hashCode();
        int hashCode5;
        if (this.userName == null) {
            hashCode5 = 0;
        }
        else {
            hashCode5 = this.userName.hashCode();
        }
        if (this.password != null) {
            hashCode = this.password.hashCode();
        }
        return (hashCode5 + ((hashCode3 + hashCode2 * 31) * 31 + hashCode4) * 31) * 31 + hashCode;
    }
    
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("voiceSipUriHostname", this.voiceSipUriHostname);
        bundle.putString("voiceAccountServerName", this.voiceAccountServerName);
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putString("userName", this.userName);
        bundle.putString("password", this.password);
        return bundle;
    }
}
