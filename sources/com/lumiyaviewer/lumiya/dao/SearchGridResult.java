// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class SearchGridResult
{

    private Long id;
    private String itemName;
    private int itemType;
    private UUID itemUUID;
    private int levensteinDistance;
    private Integer memberCount;
    private UUID searchUUID;

    public SearchGridResult()
    {
    }

    public SearchGridResult(Long long1)
    {
        id = long1;
    }

    public SearchGridResult(Long long1, UUID uuid, int i, UUID uuid1, String s, int j, Integer integer)
    {
        id = long1;
        searchUUID = uuid;
        itemType = i;
        itemUUID = uuid1;
        itemName = s;
        levensteinDistance = j;
        memberCount = integer;
    }

    public Long getId()
    {
        return id;
    }

    public String getItemName()
    {
        return itemName;
    }

    public int getItemType()
    {
        return itemType;
    }

    public UUID getItemUUID()
    {
        return itemUUID;
    }

    public int getLevensteinDistance()
    {
        return levensteinDistance;
    }

    public Integer getMemberCount()
    {
        return memberCount;
    }

    public UUID getSearchUUID()
    {
        return searchUUID;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setItemName(String s)
    {
        itemName = s;
    }

    public void setItemType(int i)
    {
        itemType = i;
    }

    public void setItemUUID(UUID uuid)
    {
        itemUUID = uuid;
    }

    public void setLevensteinDistance(int i)
    {
        levensteinDistance = i;
    }

    public void setMemberCount(Integer integer)
    {
        memberCount = integer;
    }

    public void setSearchUUID(UUID uuid)
    {
        searchUUID = uuid;
    }
}
