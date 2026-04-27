// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;


public class CachedResponse
{

    private byte data[];
    private String key;
    private boolean mustRevalidate;

    public CachedResponse()
    {
    }

    public CachedResponse(String s)
    {
        key = s;
    }

    public CachedResponse(String s, byte abyte0[], boolean flag)
    {
        key = s;
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
}
