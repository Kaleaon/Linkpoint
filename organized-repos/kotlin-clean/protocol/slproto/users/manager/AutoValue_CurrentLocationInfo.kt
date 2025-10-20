package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.voice.common.model.VoiceChannelInfo
import javax.annotation.Nullable

final class AutoValue_CurrentLocationInfo : CurrentLocationInfo() {
    private val Int inChatRangeUsers
    private val Int nearbyUsers
    private val ParcelData parcelData
    private val VoiceChannelInfo parcelVoiceChannel

    AutoValue_CurrentLocationInfo(ParcelData parcelData2, Int i, Int i2, VoiceChannelInfo voiceChannelInfo) {
        this.parcelData = parcelData2
        this.nearbyUsers = i
        this.inChatRangeUsers = i2
        this.parcelVoiceChannel = voiceChannelInfo
    }

    public Boolean equals(Object obj) {
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

    public Int hashCode() {
        Int i = 0
        Int hashCode = ((((((this.parcelData == null ? 0 : this.parcelData.hashCode()) ^ 1000003) * 1000003) ^ this.nearbyUsers) * 1000003) ^ this.inChatRangeUsers) * 1000003
        if (this.parcelVoiceChannel != null) {
            i = this.parcelVoiceChannel.hashCode()
        }
        return hashCode ^ i
    }

    public Int inChatRangeUsers() {
        return this.inChatRangeUsers
    }

    public Int nearbyUsers() {
        return this.nearbyUsers
    }

    public ParcelData parcelData() {
        return this.parcelData
    }

    public VoiceChannelInfo parcelVoiceChannel() {
        return this.parcelVoiceChannel
    }

    public String toString() {
        return "CurrentLocationInfo{parcelData=" + this.parcelData + ", " + "nearbyUsers=" + this.nearbyUsers + ", " + "inChatRangeUsers=" + this.inChatRangeUsers + ", " + "parcelVoiceChannel=" + this.parcelVoiceChannel + "}"
    }
}
