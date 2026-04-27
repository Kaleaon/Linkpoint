// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.auth;

import android.content.Intent;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class SLAuthParams
{

    public final UUID clientID;
    public final String gridName;
    public final String loginName;
    public final String loginURL;
    public final String passwordHash;
    public final String startLocation;

    public SLAuthParams(Intent intent)
    {
        loginName = intent.getStringExtra("login");
        passwordHash = intent.getStringExtra("password");
        clientID = UUIDPool.getUUID(intent.getStringExtra("client_id"));
        startLocation = intent.getStringExtra("start_location");
        loginURL = intent.getStringExtra("login_url");
        gridName = intent.getStringExtra("grid_name");
    }

    public SLAuthParams(String s, String s1, UUID uuid, String s2, String s3, String s4)
    {
        loginName = s;
        passwordHash = s1;
        clientID = uuid;
        startLocation = s2;
        loginURL = s3;
        gridName = s4;
    }

    public boolean equals(Object obj)
    {
        boolean flag = true;
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        obj = (SLAuthParams)obj;
        if (loginName == null ? ((SLAuthParams) (obj)).loginName != null : loginName.equals(((SLAuthParams) (obj)).loginName) ^ true)
        {
            return false;
        }
        if (passwordHash == null ? ((SLAuthParams) (obj)).passwordHash != null : passwordHash.equals(((SLAuthParams) (obj)).passwordHash) ^ true)
        {
            return false;
        }
        if (clientID == null ? ((SLAuthParams) (obj)).clientID != null : clientID.equals(((SLAuthParams) (obj)).clientID) ^ true)
        {
            return false;
        }
        if (startLocation == null ? ((SLAuthParams) (obj)).startLocation != null : startLocation.equals(((SLAuthParams) (obj)).startLocation) ^ true)
        {
            return false;
        }
        if (loginURL == null ? ((SLAuthParams) (obj)).loginURL != null : loginURL.equals(((SLAuthParams) (obj)).loginURL) ^ true)
        {
            return false;
        }
        if (gridName != null)
        {
            flag = gridName.equals(((SLAuthParams) (obj)).gridName);
        } else
        if (((SLAuthParams) (obj)).gridName != null)
        {
            return false;
        }
        return flag;
    }

    public int hashCode()
    {
        int j1 = 0;
        int i;
        int j;
        int k;
        int l;
        int i1;
        if (loginName != null)
        {
            i = loginName.hashCode();
        } else
        {
            i = 0;
        }
        if (passwordHash != null)
        {
            j = passwordHash.hashCode();
        } else
        {
            j = 0;
        }
        if (clientID != null)
        {
            k = clientID.hashCode();
        } else
        {
            k = 0;
        }
        if (startLocation != null)
        {
            l = startLocation.hashCode();
        } else
        {
            l = 0;
        }
        if (loginURL != null)
        {
            i1 = loginURL.hashCode();
        } else
        {
            i1 = 0;
        }
        if (gridName != null)
        {
            j1 = gridName.hashCode();
        }
        return (i1 + (l + (k + (j + i * 31) * 31) * 31) * 31) * 31 + j1;
    }

    public SLAuthParams withLocation(String s)
    {
        return new SLAuthParams(loginName, passwordHash, clientID, s, loginURL, gridName);
    }
}
