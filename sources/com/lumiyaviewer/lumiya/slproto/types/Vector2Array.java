// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            VectorArray, LLVector2

public class Vector2Array extends VectorArray
{

    public Vector2Array(int i)
    {
        super(2, i);
    }

    public Vector2Array(VectorArray vectorarray, int i)
    {
        super(vectorarray, i);
    }

    public final void add(int i, LLVector2 llvector2)
    {
        i = offset + numComponents * i;
        float af[] = data;
        int j = i + 0;
        af[j] = af[j] + llvector2.x;
        af = data;
        i++;
        af[i] = af[i] + llvector2.y;
    }

    public final void addToVector(int i, LLVector2 llvector2)
    {
        i = offset + numComponents * i;
        llvector2.x = llvector2.x + data[i + 0];
        float f = llvector2.y;
        llvector2.y = data[i + 1] + f;
    }

    public final void get(int i, LLVector2 llvector2)
    {
        i = offset + numComponents * i;
        llvector2.x = data[i + 0];
        llvector2.y = data[i + 1];
    }

    public final void getSub(int i, Vector2Array vector2array, int j, LLVector2 llvector2)
    {
        i = offset + numComponents * i;
        j = vector2array.offset + vector2array.numComponents * j;
        llvector2.x = data[i + 0] - vector2array.data[j + 0];
        llvector2.y = data[i + 1] - vector2array.data[j + 1];
    }

    public final void minMaxVector(int i, LLVector2 llvector2, LLVector2 llvector2_1)
    {
        i = offset + numComponents * i;
        float f = data[i + 0];
        float f1 = data[i + 1];
        if (llvector2.x > f)
        {
            llvector2.x = f;
        }
        if (llvector2_1.x < f)
        {
            llvector2_1.x = f;
        }
        if (llvector2.y > f1)
        {
            llvector2.y = f1;
        }
        if (llvector2_1.y < f1)
        {
            llvector2_1.y = f1;
        }
    }

    public final void minMaxVector(LLVector2 llvector2, LLVector2 llvector2_1)
    {
        int j = offset;
        for (int i = 0; i < length; i++)
        {
            float f = data[j + 0];
            float f1 = data[j + 1];
            if (llvector2.x > f)
            {
                llvector2.x = f;
            }
            if (llvector2_1.x < f)
            {
                llvector2_1.x = f;
            }
            if (llvector2.y > f1)
            {
                llvector2.y = f1;
            }
            if (llvector2_1.y < f1)
            {
                llvector2_1.y = f1;
            }
            j += numComponents;
        }

    }

    public final void set(int i, float f, float f1)
    {
        i = offset + numComponents * i;
        data[i + 0] = f;
        data[i + 1] = f1;
    }

    public void swap(int i, int j)
    {
        int k = offset;
        k = numComponents * i + k;
        i = offset;
        j = numComponents * j + i;
        for (i = 0; i < 2; i++)
        {
            float f = data[k + i];
            data[k + i] = data[j + i];
            data[j + i] = f;
        }

    }
}
