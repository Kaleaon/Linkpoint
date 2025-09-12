// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            CurrentLocationInfo

final class AutoValue_CurrentLocationInfo extends CurrentLocationInfo
{

    private final int inChatRangeUsers;
    private final int nearbyUsers;
    private final ParcelData parcelData;
    private final VoiceChannelInfo parcelVoiceChannel;

    AutoValue_CurrentLocationInfo(ParcelData parceldata, int i, int j, VoiceChannelInfo voicechannelinfo)
    {
        parcelData = parceldata;
        nearbyUsers = i;
        inChatRangeUsers = j;
        parcelVoiceChannel = voicechannelinfo;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof CurrentLocationInfo)
        {
            obj = (CurrentLocationInfo)obj;
            if ((parcelData != null ? parcelData.equals(((CurrentLocationInfo) (obj)).parcelData()) : ((CurrentLocationInfo) (obj)).parcelData() == null) && (nearbyUsers == ((CurrentLocationInfo) (obj)).nearbyUsers() && inChatRangeUsers == ((CurrentLocationInfo) (obj)).inChatRangeUsers()))
            {
                if (parcelVoiceChannel == null)
                {
                    return ((CurrentLocationInfo) (obj)).parcelVoiceChannel() == null;
                } else
                {
                    return parcelVoiceChannel.equals(((CurrentLocationInfo) (obj)).parcelVoiceChannel());
                }
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int j = 0;
        int i;
        int k;
        int l;
        if (parcelData == null)
        {
            i = 0;
        } else
        {
            i = parcelData.hashCode();
        }
        k = nearbyUsers;
        l = inChatRangeUsers;
        if (parcelVoiceChannel != null)
        {
            j = parcelVoiceChannel.hashCode();
        }
        return (((i ^ 0xf4243) * 0xf4243 ^ k) * 0xf4243 ^ l) * 0xf4243 ^ j;
    }

    public int inChatRangeUsers()
    {
        return inChatRangeUsers;
    }

    public int nearbyUsers()
    {
        return nearbyUsers;
    }

    public ParcelData parcelData()
    {
        return parcelData;
    }

    public VoiceChannelInfo parcelVoiceChannel()
    {
        return parcelVoiceChannel;
    }

    public String toString()
    {
        return (new StringBuilder()).append("CurrentLocationInfo{parcelData=").append(parcelData).append(", ").append("nearbyUsers=").append(nearbyUsers).append(", ").append("inChatRangeUsers=").append(inChatRangeUsers).append(", ").append("parcelVoiceChannel=").append(parcelVoiceChannel).append("}").toString();
    }
}
