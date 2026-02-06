package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import java.util.UUID;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
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
        if (this != obj) {
            if (obj != null && getClass() == obj.getClass()) {
                VoiceLoginInfo voiceLoginInfo = (VoiceLoginInfo) obj;
                if (this.voiceSipUriHostname != null ? this.voiceSipUriHostname.equals(voiceLoginInfo.voiceSipUriHostname) : voiceLoginInfo.voiceSipUriHostname == null) {
                    if (this.voiceAccountServerName != null ? this.voiceAccountServerName.equals(voiceLoginInfo.voiceAccountServerName) : voiceLoginInfo.voiceAccountServerName == null) {
                        if (this.agentUUID.equals(voiceLoginInfo.agentUUID)) {
                            if (this.userName != null ? this.userName.equals(voiceLoginInfo.userName) : voiceLoginInfo.userName == null) {
                                return this.password == null ? voiceLoginInfo.password == null : this.password.equals(voiceLoginInfo.password);
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((this.userName == null ? 0 : this.userName.hashCode()) + (((((this.voiceAccountServerName == null ? 0 : this.voiceAccountServerName.hashCode()) + ((this.voiceSipUriHostname == null ? 0 : this.voiceSipUriHostname.hashCode()) * 31)) * 31) + this.agentUUID.hashCode()) * 31)) * 31) + (this.password != null ? this.password.hashCode() : 0);
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
