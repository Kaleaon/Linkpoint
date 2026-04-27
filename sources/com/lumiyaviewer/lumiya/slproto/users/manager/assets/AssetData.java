// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;


public class AssetData
{

    private final byte data[];
    private final int status;

    public AssetData(int i, byte abyte0[])
    {
        status = i;
        data = abyte0;
    }

    public byte[] getData()
    {
        return data;
    }

    public int getStatus()
    {
        return status;
    }
}
