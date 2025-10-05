package com.linkpoint.slproto.users.manager;

import com.linkpoint.slproto.users.ParcelData;
import com.linkpoint.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nullable;

public abstract class CurrentLocationInfo {
    public static CurrentLocationInfo create(@Nullable ParcelData parcelData, int i, int i2, @Nullable VoiceChannelInfo voiceChannelInfo) {
        return new AutoValue_CurrentLocationInfo(parcelData, i, i2, voiceChannelInfo);
    }

    public abstract int inChatRangeUsers();

    public abstract int nearbyUsers();

    @Nullable
    public abstract ParcelData parcelData();

    @Nullable
    public abstract VoiceChannelInfo parcelVoiceChannel();
}
