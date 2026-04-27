// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.util.UUID;

public class UserName
{

    private String displayName;
    private boolean isBadUUID;
    private String userName;
    private UUID uuid;

    public UserName()
    {
    }

    public UserName(UUID uuid1)
    {
        uuid = uuid1;
    }

    public UserName(UUID uuid1, String s, String s1, boolean flag)
    {
        uuid = uuid1;
        userName = s;
        displayName = s1;
        isBadUUID = flag;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public boolean getIsBadUUID()
    {
        return isBadUUID;
    }

    public String getUserName()
    {
        return userName;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public boolean isComplete()
    {
        if (!isBadUUID)
        {
            if (!Strings.isNullOrEmpty(userName))
            {
                return Strings.isNullOrEmpty(displayName) ^ true;
            } else
            {
                return false;
            }
        } else
        {
            return true;
        }
    }

    public boolean mergeWith(UserName username)
    {
        boolean flag1 = false;
        if (username.isBadUUID && isBadUUID ^ true)
        {
            isBadUUID = true;
            return true;
        }
        if (isBadUUID)
        {
            return false;
        }
        boolean flag = flag1;
        if (!Strings.isNullOrEmpty(username.userName))
        {
            flag = flag1;
            if (Objects.equal(userName, username.userName) ^ true)
            {
                userName = username.userName;
                flag = true;
            }
        }
        flag1 = flag;
        if (!Strings.isNullOrEmpty(username.displayName))
        {
            flag1 = flag;
            if (Objects.equal(displayName, username.displayName) ^ true)
            {
                displayName = username.displayName;
                flag1 = true;
            }
        }
        return flag1;
    }

    public void setDisplayName(String s)
    {
        displayName = s;
    }

    public void setIsBadUUID(boolean flag)
    {
        isBadUUID = flag;
    }

    public void setUserName(String s)
    {
        userName = s;
    }

    public void setUuid(UUID uuid1)
    {
        uuid = uuid1;
    }
}
