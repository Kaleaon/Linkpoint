package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.slproto.users.ParcelData
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import javax.annotation.Nullable

class AutoValue_CurrentLocationInfo : CurrentLocationInfo {
    private Int inChatRangeUsers
    private Int nearbyUsers
    private ParcelData parcelData
    private VoiceChannelInfo parcelVoiceChannel

    AutoValue_CurrentLocationInfo(@Nullable ParcelData parcelData2, Int i, Int i2, @Nullable VoiceChannelInfo voiceChannelInfo) {
        this.parcelData = parcelData2
        this.nearbyUsers = i
        this.inChatRangeUsers = i2
        this.parcelVoiceChannel = voiceChannelInfo
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof CurrentLocationInfo)) {
            return false
        }
        CurrentLocationInfo currentLocationInfo = (CurrentLocationInfo) obj
        if (this.parcelData != null ? this.parcelData.equals(currentLocationInfo.parcelData()) : currentLocationInfo.parcelData() == null) {
            if (this.nearbyUsers == currentLocationInfo.nearbyUsers() && this.inChatRangeUsers == currentLocationInfo.inChatRangeUsers()) {
                return this.parcelVoiceChannel == null ? currentLocationInfo.parcelVoiceChannel() == null : this.parcelVoiceChannel.equals(currentLocationInfo.parcelVoiceChannel())
            }
        }
        return false
    }

    Int hashCode() {
        Int i = 0
        Int hashCode = ((((((this.parcelData == null ? 0 : this.parcelData.hashCode()) ^ 1000003) * 1000003) ^ this.nearbyUsers) * 1000003) ^ this.inChatRangeUsers) * 1000003
        if (this.parcelVoiceChannel != null) {
            i = this.parcelVoiceChannel.hashCode()
        }
        return hashCode ^ i
    }

    Int inChatRangeUsers() {
        return this.inChatRangeUsers
    }

    Int nearbyUsers() {
        return this.nearbyUsers
    }

    @Nullable
    ParcelData parcelData() {
        return this.parcelData
    }

    @Nullable
    VoiceChannelInfo parcelVoiceChannel() {
        return this.parcelVoiceChannel
    }

    String toString() {
        return "CurrentLocationInfo{parcelData=" + this.parcelData + ", " + "nearbyUsers=" + this.nearbyUsers + ", " + "inChatRangeUsers=" + this.inChatRangeUsers + ", " + "parcelVoiceChannel=" + this.parcelVoiceChannel + "}"
    }
}
