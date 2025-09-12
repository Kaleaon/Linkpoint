// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;


public class AssetFormatException extends Exception
{

    private static final long serialVersionUID = 0x8b8bac249a6857e6L;

    public AssetFormatException()
    {
        super("Unsupported asset format");
    }

    public AssetFormatException(String s)
    {
        super(s);
    }

    public AssetFormatException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
