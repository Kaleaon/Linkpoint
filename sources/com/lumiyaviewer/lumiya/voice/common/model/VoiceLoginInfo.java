// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;
import java.util.UUID;

public class VoiceLoginInfo
{

    public final UUID agentUUID;
    public final String password;
    public final String userName;
    public final String voiceAccountServerName;
    public final String voiceSipUriHostname;

    public VoiceLoginInfo(Bundle bundle)
    {
        voiceSipUriHostname = bundle.getString("voiceSipUriHostname");
        voiceAccountServerName = bundle.getString("voiceAccountServerName");
        agentUUID = UUID.fromString(bundle.getString("agentUUID"));
        userName = bundle.getString("userName");
        password = bundle.getString("password");
    }

    public VoiceLoginInfo(String s, String s1, UUID uuid, String s2, String s3)
    {
        voiceSipUriHostname = s;
        voiceAccountServerName = s1;
        agentUUID = uuid;
        userName = s2;
        password = s3;
    }

    public boolean equals(Object obj)
    {
        if (this != obj)
        {
            if (obj == null || getClass() != obj.getClass())
            {
                return false;
            }
            obj = (VoiceLoginInfo)obj;
        } else
        {
            return true;
        }
        if (voiceSipUriHostname != null ? voiceSipUriHostname.equals(((VoiceLoginInfo) (obj)).voiceSipUriHostname) : ((VoiceLoginInfo) (obj)).voiceSipUriHostname == null)
        {
            if (voiceAccountServerName != null ? voiceAccountServerName.equals(((VoiceLoginInfo) (obj)).voiceAccountServerName) : ((VoiceLoginInfo) (obj)).voiceAccountServerName == null)
            {
                if (agentUUID.equals(((VoiceLoginInfo) (obj)).agentUUID))
                {
                    if (userName != null ? userName.equals(((VoiceLoginInfo) (obj)).userName) : ((VoiceLoginInfo) (obj)).userName == null)
                    {
                        if (password == null)
                        {
                            return ((VoiceLoginInfo) (obj)).password == null;
                        } else
                        {
                            return password.equals(((VoiceLoginInfo) (obj)).password);
                        }
                    } else
                    {
                        return false;
                    }
                } else
                {
                    return false;
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
        int l = 0;
        int i;
        int j;
        int k;
        int i1;
        if (voiceSipUriHostname == null)
        {
            i = 0;
        } else
        {
            i = voiceSipUriHostname.hashCode();
        }
        if (voiceAccountServerName == null)
        {
            j = 0;
        } else
        {
            j = voiceAccountServerName.hashCode();
        }
        i1 = agentUUID.hashCode();
        if (userName == null)
        {
            k = 0;
        } else
        {
            k = userName.hashCode();
        }
        if (password != null)
        {
            l = password.hashCode();
        }
        return (k + ((j + i * 31) * 31 + i1) * 31) * 31 + l;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putString("voiceSipUriHostname", voiceSipUriHostname);
        bundle.putString("voiceAccountServerName", voiceAccountServerName);
        bundle.putString("agentUUID", agentUUID.toString());
        bundle.putString("userName", userName);
        bundle.putString("password", password);
        return bundle;
    }
}
