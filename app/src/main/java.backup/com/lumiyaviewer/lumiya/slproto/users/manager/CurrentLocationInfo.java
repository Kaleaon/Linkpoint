package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
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
