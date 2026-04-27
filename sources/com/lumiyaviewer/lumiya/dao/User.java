// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import java.util.UUID;

public class User
    implements ChatterDisplayInfo
{

    private boolean badUUID;
    private String displayName;
    private Long id;
    private boolean isFriend;
    private int rightsGiven;
    private int rightsHas;
    private String userName;
    private UUID uuid;

    public User()
    {
    }

    public User(Long long1)
    {
        id = long1;
    }

    public User(Long long1, UUID uuid1, String s, String s1, boolean flag, boolean flag1, int i, 
            int j)
    {
        id = long1;
        uuid = uuid1;
        userName = s;
        displayName = s1;
        badUUID = flag;
        isFriend = flag1;
        rightsGiven = i;
        rightsHas = j;
    }

    public void buildView(Context context, ChatterItemViewBuilder chatteritemviewbuilder, UserManager usermanager)
    {
        chatteritemviewbuilder.setLabel(displayName);
        chatteritemviewbuilder.setThumbnailChatterID(getChatterID(usermanager), displayName);
    }

    public boolean getBadUUID()
    {
        return badUUID;
    }

    public ChatterID getChatterID(UserManager usermanager)
    {
        return ChatterID.getUserChatterID(usermanager.getUserID(), uuid);
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Long getId()
    {
        return id;
    }

    public boolean getIsFriend()
    {
        return isFriend;
    }

    public int getRightsGiven()
    {
        return rightsGiven;
    }

    public int getRightsHas()
    {
        return rightsHas;
    }

    public String getUserName()
    {
        return userName;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public boolean nameNeedsFetching()
    {
        if (userName == null || displayName == null)
        {
            return badUUID ^ true;
        } else
        {
            return false;
        }
    }

    public void setBadUUID(boolean flag)
    {
        badUUID = flag;
    }

    public void setDisplayName(String s)
    {
        displayName = s;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setIsFriend(boolean flag)
    {
        isFriend = flag;
    }

    public void setRightsGiven(int i)
    {
        rightsGiven = i;
    }

    public void setRightsHas(int i)
    {
        rightsHas = i;
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
