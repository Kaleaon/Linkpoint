// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;


public class CachedAsset
{

    private byte data[];
    private String key;
    private boolean mustRevalidate;
    private int status;

    public CachedAsset()
    {
    }

    public CachedAsset(String s)
    {
        key = s;
    }

    public CachedAsset(String s, int i, byte abyte0[], boolean flag)
    {
        key = s;
        status = i;
        data = abyte0;
        mustRevalidate = flag;
    }

    public byte[] getData()
    {
        return data;
    }

    public String getKey()
    {
        return key;
    }

    public boolean getMustRevalidate()
    {
        return mustRevalidate;
    }

    public int getStatus()
    {
        return status;
    }

    public void setData(byte abyte0[])
    {
        data = abyte0;
    }

    public void setKey(String s)
    {
        key = s;
    }

    public void setMustRevalidate(boolean flag)
    {
        mustRevalidate = flag;
    }

    public void setStatus(int i)
    {
        status = i;
    }
}
