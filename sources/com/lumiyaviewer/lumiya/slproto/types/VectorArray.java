// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


public class VectorArray
{

    protected float data[];
    protected int length;
    protected int numComponents;
    protected int offset;

    public VectorArray(int i, int j)
    {
        data = new float[i * j];
        numComponents = i;
        length = j;
        offset = 0;
    }

    public VectorArray(VectorArray vectorarray, int i)
    {
        data = vectorarray.data;
        numComponents = vectorarray.numComponents;
        length = vectorarray.length;
        offset = i;
    }

    public final float[] getData()
    {
        return data;
    }

    public final int getElementOffset(int i)
    {
        return offset + numComponents * i;
    }

    public final int getLength()
    {
        return length;
    }

    public final int getNumComponents()
    {
        return numComponents;
    }
}
