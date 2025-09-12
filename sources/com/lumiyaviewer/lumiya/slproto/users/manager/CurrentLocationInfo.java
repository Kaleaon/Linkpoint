// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            AutoValue_CurrentLocationInfo

public abstract class CurrentLocationInfo
{

    public CurrentLocationInfo()
    {
    }

    public static CurrentLocationInfo create(ParcelData parceldata, int i, int j, VoiceChannelInfo voicechannelinfo)
    {
        return new AutoValue_CurrentLocationInfo(parceldata, i, j, voicechannelinfo);
    }

    public abstract int inChatRangeUsers();

    public abstract int nearbyUsers();

    public abstract ParcelData parcelData();

    public abstract VoiceChannelInfo parcelVoiceChannel();
}
