// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;


public class UnsupportedObjectTypeException extends Exception
{

    public UnsupportedObjectTypeException(byte byte0)
    {
        super(String.format("Unsupported object type: 0x%x", new Object[] {
            Byte.valueOf(byte0)
        }));
    }
}
