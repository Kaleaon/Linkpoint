package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.voice.common.model.VoiceChannelInfo
import androidx.annotation.Nullable

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

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is CurrentLocationInfo)) {
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

    fun hashCode(): Int {
        var i: Int = 0
        var hashCode: Int = ((((((this.parcelData == null ? 0 : this.parcelData.hashCode()) ^ 1000003) * 1000003) ^ this.nearbyUsers) * 1000003) ^ this.inChatRangeUsers) * 1000003
        if (this.parcelVoiceChannel != null) {
            i = this.parcelVoiceChannel.hashCode()
        }
        return hashCode ^ i
    }

    fun inChatRangeUsers(): Int {
        return this.inChatRangeUsers
    }

    fun nearbyUsers(): Int {
        return this.nearbyUsers
    }

    @Nullable
    fun parcelData(): ParcelData {
        return this.parcelData
    }

    @Nullable
    fun parcelVoiceChannel(): VoiceChannelInfo {
        return this.parcelVoiceChannel
    }

    fun toString(): String {
        return "CurrentLocationInfo{parcelData=" + this.parcelData + ", " + "nearbyUsers=" + this.nearbyUsers + ", " + "inChatRangeUsers=" + this.inChatRangeUsers + ", " + "parcelVoiceChannel=" + this.parcelVoiceChannel + "}"
    }
}
