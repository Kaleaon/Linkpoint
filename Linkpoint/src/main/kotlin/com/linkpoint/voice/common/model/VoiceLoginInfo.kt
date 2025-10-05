package com.linkpoint.voice.common.model

import android.os.Bundle
import java.util.UUID
import javax.annotation.Nonnull

class VoiceLoginInfo {
    val UUID agentUUID
    val String password
    val String userName
    val String voiceAccountServerName
    val String voiceSipUriHostname

    public VoiceLoginInfo(Bundle bundle) {
        this.voiceSipUriHostname = bundle.getString("voiceSipUriHostname")
        this.voiceAccountServerName = bundle.getString("voiceAccountServerName")
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"))
        this.userName = bundle.getString("userName")
        this.password = bundle.getString("password")
    }

    public VoiceLoginInfo(String string2, String string3, UUID uUID, String string4, String string5) {
        this.voiceSipUriHostname = string2
        this.voiceAccountServerName = string3
        this.agentUUID = uUID
        this.userName = string4
        this.password = string5
    }

    /*
     * Enabled aggressive block sorting
     */
    public Boolean equals(Object object) {
        Boolean bl = true
        Boolean bl2 = false
        if (this == object) {
            return true
        }
        Boolean bl3 = bl2
        if (object == null) return bl3
        bl3 = bl2
        if (this.getClass() != object.getClass()) return bl3
        object = (VoiceLoginInfo)object
        if (this.voiceSipUriHostname != null) {
            bl3 = bl2
            if (!this.voiceSipUriHostname.equals(((VoiceLoginInfo)object).voiceSipUriHostname)) return bl3
        } else if (((VoiceLoginInfo)object).voiceSipUriHostname != null) {
            return bl2
        }
        if (this.voiceAccountServerName != null) {
            bl3 = bl2
            if (!this.voiceAccountServerName.equals(((VoiceLoginInfo)object).voiceAccountServerName)) return bl3
        } else if (((VoiceLoginInfo)object).voiceAccountServerName != null) {
            return bl2
        }
        bl3 = bl2
        if (!this.agentUUID.equals(((VoiceLoginInfo)object).agentUUID)) return bl3
        if (this.userName != null) {
            bl3 = bl2
            if (!this.userName.equals(((VoiceLoginInfo)object).userName)) return bl3
        } else if (((VoiceLoginInfo)object).userName != null) {
            return bl2
        }
        if (this.password != null) {
            return this.password.equals(((VoiceLoginInfo)object).password)
        }
        bl3 = bl
        if (((VoiceLoginInfo)object).password == null) return bl3
        return false
    }

    /*
     * Enabled aggressive block sorting
     */
    public Int hashCode() {
        Int n = 0
        Int n2 = this.voiceSipUriHostname != null ? this.voiceSipUriHostname.hashCode() : 0
        Int n3 = this.voiceAccountServerName != null ? this.voiceAccountServerName.hashCode() : 0
        Int n4 = this.agentUUID.hashCode()
        Int n5 = this.userName != null ? this.userName.hashCode() : 0
        if (this.password != null) {
            n = this.password.hashCode()
        }
        return (((n2 * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n
    }

    public Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putString("voiceSipUriHostname", this.voiceSipUriHostname)
        bundle.putString("voiceAccountServerName", this.voiceAccountServerName)
        bundle.putString("agentUUID", this.agentUUID.toString())
        bundle.putString("userName", this.userName)
        bundle.putString("password", this.password)
        return bundle
    }
}

