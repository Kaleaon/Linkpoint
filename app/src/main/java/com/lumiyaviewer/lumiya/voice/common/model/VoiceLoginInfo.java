package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import java.util.UUID;
import javax.annotation.Nonnull;

public class VoiceLoginInfo {
    @Nonnull
    public final UUID agentUUID;
    public final String password;
    public final String userName;
    public final String voiceAccountServerName;
    public final String voiceSipUriHostname;

    public VoiceLoginInfo(Bundle bundle) {
        this.voiceSipUriHostname = bundle.getString("voiceSipUriHostname");
        this.voiceAccountServerName = bundle.getString("voiceAccountServerName");
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        this.userName = bundle.getString("userName");
        this.password = bundle.getString("password");
    }

    public VoiceLoginInfo(String str, String str2, @Nonnull UUID uuid, String str3, String str4) {
        this.voiceSipUriHostname = str;
        this.voiceAccountServerName = str2;
        this.agentUUID = uuid;
        this.userName = str3;
        this.password = str4;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VoiceLoginInfo voiceLoginInfo = (VoiceLoginInfo) obj;
        if (this.voiceSipUriHostname != null ? !this.voiceSipUriHostname.equals(voiceLoginInfo.voiceSipUriHostname) : voiceLoginInfo.voiceSipUriHostname != null) {
            return false;
        }
        if (this.voiceAccountServerName != null ? !this.voiceAccountServerName.equals(voiceLoginInfo.voiceAccountServerName) : voiceLoginInfo.voiceAccountServerName != null) {
            return false;
        }
        if (!this.agentUUID.equals(voiceLoginInfo.agentUUID)) {
            return false;
        }
        if (this.userName != null ? this.userName.equals(voiceLoginInfo.userName) : voiceLoginInfo.userName == null) {
            return this.password == null ? voiceLoginInfo.password == null : this.password.equals(voiceLoginInfo.password);
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((this.userName == null ? 0 : this.userName.hashCode()) + (((((this.voiceAccountServerName == null ? 0 : this.voiceAccountServerName.hashCode()) + ((this.voiceSipUriHostname == null ? 0 : this.voiceSipUriHostname.hashCode()) * 31)) * 31) + this.agentUUID.hashCode()) * 31)) * 31;
        if (this.password != null) {
            i = this.password.hashCode();
        }
        return hashCode + i;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("voiceSipUriHostname", this.voiceSipUriHostname);
        bundle.putString("voiceAccountServerName", this.voiceAccountServerName);
        bundle.putString("agentUUID", this.agentUUID.toString());
        bundle.putString("userName", this.userName);
        bundle.putString("password", this.password);
        return bundle;
    }
}
