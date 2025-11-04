// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;

final class AutoValue_CurrentLocationInfo extends CurrentLocationInfo
{
    private final int inChatRangeUsers;
    private final int nearbyUsers;
    private final ParcelData parcelData;
    private final VoiceChannelInfo parcelVoiceChannel;
    
    AutoValue_CurrentLocationInfo(@Nullable final ParcelData parcelData, final int nearbyUsers, final int inChatRangeUsers, @Nullable final VoiceChannelInfo parcelVoiceChannel) {
        this.parcelData = parcelData;
        this.nearbyUsers = nearbyUsers;
        this.inChatRangeUsers = inChatRangeUsers;
        this.parcelVoiceChannel = parcelVoiceChannel;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (o == this) {
            return true;
        }
        if (o instanceof CurrentLocationInfo) {
            final CurrentLocationInfo currentLocationInfo = (CurrentLocationInfo)o;
            if (this.parcelData == null) {
                if (currentLocationInfo.parcelData() != null) {
                    return false;
                }
            }
            else if (!this.parcelData.equals(currentLocationInfo.parcelData())) {
                return false;
            }
            if (this.nearbyUsers != currentLocationInfo.nearbyUsers() || this.inChatRangeUsers != currentLocationInfo.inChatRangeUsers()) {
                return false;
            }
            if (this.parcelVoiceChannel == null) {
                if (currentLocationInfo.parcelVoiceChannel() != null) {
                    equals = false;
                }
            }
            else {
                equals = this.parcelVoiceChannel.equals(currentLocationInfo.parcelVoiceChannel());
            }
            return equals;
            equals = false;
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        int hashCode2;
        if (this.parcelData == null) {
            hashCode2 = 0;
        }
        else {
            hashCode2 = this.parcelData.hashCode();
        }
        final int nearbyUsers = this.nearbyUsers;
        final int inChatRangeUsers = this.inChatRangeUsers;
        if (this.parcelVoiceChannel != null) {
            hashCode = this.parcelVoiceChannel.hashCode();
        }
        return (((hashCode2 ^ 0xF4243) * 1000003 ^ nearbyUsers) * 1000003 ^ inChatRangeUsers) * 1000003 ^ hashCode;
    }
    
    @Override
    public int inChatRangeUsers() {
        return this.inChatRangeUsers;
    }
    
    @Override
    public int nearbyUsers() {
        return this.nearbyUsers;
    }
    
    @Nullable
    @Override
    public ParcelData parcelData() {
        return this.parcelData;
    }
    
    @Nullable
    @Override
    public VoiceChannelInfo parcelVoiceChannel() {
        return this.parcelVoiceChannel;
    }
    
    @Override
    public String toString() {
        return "CurrentLocationInfo{parcelData=" + this.parcelData + ", " + "nearbyUsers=" + this.nearbyUsers + ", " + "inChatRangeUsers=" + this.inChatRangeUsers + ", " + "parcelVoiceChannel=" + this.parcelVoiceChannel + "}";
    }
}
