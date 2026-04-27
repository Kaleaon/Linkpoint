// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;


public class MuteListCachedData
{

    private int CRC;
    private byte data[];
    private Long id;

    public MuteListCachedData()
    {
    }

    public MuteListCachedData(Long long1)
    {
        id = long1;
    }

    public MuteListCachedData(Long long1, int i, byte abyte0[])
    {
        id = long1;
        CRC = i;
        data = abyte0;
    }

    public int getCRC()
    {
        return CRC;
    }

    public byte[] getData()
    {
        return data;
    }

    public Long getId()
    {
        return id;
    }

    public void setCRC(int i)
    {
        CRC = i;
    }

    public void setData(byte abyte0[])
    {
        data = abyte0;
    }

    public void setId(Long long1)
    {
        id = long1;
    }
}
