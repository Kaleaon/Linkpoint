// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.net.Uri;
import android.os.Bundle;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.lumiyaviewer.lumiya.base64.Base64;
import java.util.Arrays;
import java.util.UUID;

public class VoiceChannelInfo
{

    public final boolean isConference;
    public final boolean isSpatial;
    public final String voiceChannelURI;

    public VoiceChannelInfo(Uri uri)
    {
        voiceChannelURI = uri.getQueryParameter("voiceChannelURI");
        isSpatial = Objects.equal(uri.getQueryParameter("isSpatial"), "true");
        isConference = Objects.equal(uri.getQueryParameter("isConference"), "true");
    }

    public VoiceChannelInfo(Bundle bundle)
    {
        voiceChannelURI = bundle.getString("voiceChannelURI");
        isSpatial = bundle.getBoolean("isSpatial");
        isConference = bundle.getBoolean("isConference");
    }

    public VoiceChannelInfo(String s, boolean flag, boolean flag1)
    {
        voiceChannelURI = s;
        isSpatial = flag;
        isConference = flag1;
    }

    public VoiceChannelInfo(UUID uuid, String s)
    {
        uuid = Bytes.concat(new byte[][] {
            Longs.toByteArray(uuid.getMostSignificantBits()), Longs.toByteArray(uuid.getLeastSignificantBits())
        });
        uuid = (new StringBuilder()).append("x").append(Base64.encodeToString(uuid, false).replace('+', '-').replace('/', '_')).toString();
        voiceChannelURI = (new StringBuilder()).append("sip:").append(uuid).append("@").append(s).toString();
        isSpatial = false;
        isConference = false;
    }

    public static UUID agentUUIDFromURI(String s)
    {
        s = Strings.nullToEmpty(s);
        int i = s.indexOf(':');
        if (i != -1)
        {
            s = s.substring(i + 1);
        }
        i = s.indexOf('@');
        if (i != -1)
        {
            s = s.substring(0, i);
        }
        if (s.startsWith("x"))
        {
            s = s.substring(1);
        }
        for (s = Base64.decode(s.replace("-", "+").replace("_", "/")); s == null || s.length != 16;)
        {
            return null;
        }

        return new UUID(Longs.fromByteArray(Arrays.copyOfRange(s, 0, 8)), Longs.fromByteArray(Arrays.copyOfRange(s, 8, 16)));
    }

    public void appendToUri(android.net.Uri.Builder builder)
    {
        builder.appendQueryParameter("voiceChannelURI", voiceChannelURI);
        String s;
        if (!isSpatial)
        {
            s = "false";
        } else
        {
            s = "true";
        }
        builder.appendQueryParameter("isSpatial", s);
        if (!isConference)
        {
            s = "false";
        } else
        {
            s = "true";
        }
        builder.appendQueryParameter("isConference", s);
    }

    public boolean equals(Object obj)
    {
        if (this != obj)
        {
            if (obj == null || getClass() != obj.getClass())
            {
                return false;
            }
            obj = (VoiceChannelInfo)obj;
        } else
        {
            return true;
        }
        if (isSpatial == ((VoiceChannelInfo) (obj)).isSpatial)
        {
            if (isConference == ((VoiceChannelInfo) (obj)).isConference)
            {
                if (voiceChannelURI == null)
                {
                    return ((VoiceChannelInfo) (obj)).voiceChannelURI == null;
                } else
                {
                    return voiceChannelURI.equals(((VoiceChannelInfo) (obj)).voiceChannelURI);
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

    public UUID getAgentUUID()
    {
        return agentUUIDFromURI(voiceChannelURI);
    }

    public int hashCode()
    {
        int k = 0;
        int i;
        int j;
        if (voiceChannelURI == null)
        {
            i = 0;
        } else
        {
            i = voiceChannelURI.hashCode();
        }
        if (!isSpatial)
        {
            j = 0;
        } else
        {
            j = 1;
        }
        if (isConference)
        {
            k = 1;
        }
        return (j + i * 31) * 31 + k;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("voiceChannelURI", voiceChannelURI);
        bundle.putBoolean("isSpatial", isSpatial);
        bundle.putBoolean("isConference", isConference);
        return bundle;
    }
}
