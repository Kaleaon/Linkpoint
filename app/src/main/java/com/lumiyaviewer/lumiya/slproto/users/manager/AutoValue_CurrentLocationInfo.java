package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nullable;

final class AutoValue_CurrentLocationInfo extends CurrentLocationInfo {
    private final int inChatRangeUsers;
    private final int nearbyUsers;
    private final ParcelData parcelData;
    private final VoiceChannelInfo parcelVoiceChannel;

    AutoValue_CurrentLocationInfo(@Nullable ParcelData parcelData2, int i, int i2, @Nullable VoiceChannelInfo voiceChannelInfo) {
        this.parcelData = parcelData2;
        this.nearbyUsers = i;
        this.inChatRangeUsers = i2;
        this.parcelVoiceChannel = voiceChannelInfo;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CurrentLocationInfo)) {
            return false;
        }
        CurrentLocationInfo currentLocationInfo = (CurrentLocationInfo) obj;
        if (this.parcelData != null ? this.parcelData.equals(currentLocationInfo.parcelData()) : currentLocationInfo.parcelData() == null) {
            if (this.nearbyUsers == currentLocationInfo.nearbyUsers() && this.inChatRangeUsers == currentLocationInfo.inChatRangeUsers()) {
                return this.parcelVoiceChannel == null ? currentLocationInfo.parcelVoiceChannel() == null : this.parcelVoiceChannel.equals(currentLocationInfo.parcelVoiceChannel());
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = ((((((this.parcelData == null ? 0 : this.parcelData.hashCode()) ^ 1000003) * 1000003) ^ this.nearbyUsers) * 1000003) ^ this.inChatRangeUsers) * 1000003;
        if (this.parcelVoiceChannel != null) {
            i = this.parcelVoiceChannel.hashCode();
        }
        return hashCode ^ i;
    }

    public int inChatRangeUsers() {
        return this.inChatRangeUsers;
    }

    public int nearbyUsers() {
        return this.nearbyUsers;
    }

    @Nullable
    public ParcelData parcelData() {
        return this.parcelData;
    }

    @Nullable
    public VoiceChannelInfo parcelVoiceChannel() {
        return this.parcelVoiceChannel;
    }

    public String toString() {
        return "CurrentLocationInfo{parcelData=" + this.parcelData + ", " + "nearbyUsers=" + this.nearbyUsers + ", " + "inChatRangeUsers=" + this.inChatRangeUsers + ", " + "parcelVoiceChannel=" + this.parcelVoiceChannel + "}";
    }
}
