// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;

public abstract class CurrentLocationInfo
{
    public static CurrentLocationInfo create(@Nullable final ParcelData parcelData, final int n, final int n2, @Nullable final VoiceChannelInfo voiceChannelInfo) {
        return new AutoValue_CurrentLocationInfo(parcelData, n, n2, voiceChannelInfo);
    }
    
    public abstract int inChatRangeUsers();
    
    public abstract int nearbyUsers();
    
    @Nullable
    public abstract ParcelData parcelData();
    
    @Nullable
    public abstract VoiceChannelInfo parcelVoiceChannel();
}
