// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;


public class UserPic
{

    private byte bitmap[];
    private Long id;
    private String uuid;

    public UserPic()
    {
    }

    public UserPic(Long long1)
    {
        id = long1;
    }

    public UserPic(Long long1, String s, byte abyte0[])
    {
        id = long1;
        uuid = s;
        bitmap = abyte0;
    }

    public byte[] getBitmap()
    {
        return bitmap;
    }

    public Long getId()
    {
        return id;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setBitmap(byte abyte0[])
    {
        bitmap = abyte0;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setUuid(String s)
    {
        uuid = s;
    }
}
