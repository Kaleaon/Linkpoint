package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
final class AutoValue_CurrentLocationInfo extends CurrentLocationInfo {
    private final int inChatRangeUsers;
    private final int nearbyUsers;
    private final ParcelData parcelData;
    private final VoiceChannelInfo parcelVoiceChannel;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_CurrentLocationInfo(@Nullable ParcelData parcelData, int i, int i2, @Nullable VoiceChannelInfo voiceChannelInfo) {
        this.parcelData = parcelData;
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
        return (((((((this.parcelData == null ? 0 : this.parcelData.hashCode()) ^ 1000003) * 1000003) ^ this.nearbyUsers) * 1000003) ^ this.inChatRangeUsers) * 1000003) ^ (this.parcelVoiceChannel != null ? this.parcelVoiceChannel.hashCode() : 0);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
    public int inChatRangeUsers() {
        return this.inChatRangeUsers;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
    public int nearbyUsers() {
        return this.nearbyUsers;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
    @Nullable
    public ParcelData parcelData() {
        return this.parcelData;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
    @Nullable
    public VoiceChannelInfo parcelVoiceChannel() {
        return this.parcelVoiceChannel;
    }

    public String toString() {
        return "CurrentLocationInfo{parcelData=" + this.parcelData + ", nearbyUsers=" + this.nearbyUsers + ", inChatRangeUsers=" + this.inChatRangeUsers + ", parcelVoiceChannel=" + this.parcelVoiceChannel + "}";
    }
}
