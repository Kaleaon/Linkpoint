// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;


public class FrustrumPlanes
{

    public static final int INSIDE = 1;
    public static final int INTERSECT = 0;
    private static final int NUM_PLANES = 6;
    public static final int OUTSIDE = -1;
    private final float params[] = new float[24];
    private final int pnIndex[] = new int[36];

    public FrustrumPlanes(float af[])
    {
        int i = 0;
        while (i < 6) 
        {
            int j = i / 2;
            float f;
            if ((i & 1) != 0)
            {
                f = -1F;
            } else
            {
                f = 1.0F;
            }
            initPlane(i, af, 2 - j, f);
            i++;
        }
    }

    private void initPlane(int i, float af[], int j, float f)
    {
        boolean flag = false;
        int i1 = i * 4;
        for (int k = 0; k < 4; k++)
        {
            params[i1 + k] = af[k * 4 + 3] + af[k * 4 + j] * f;
        }

        j = 0;
        f = 0.0F;
        for (; j < 3; j++)
        {
            float f1 = params[i1 + j];
            f += f1 * f1;
        }

        f = (float)Math.sqrt(f);
        int l = 0;
        do
        {
            j = ((flag) ? 1 : 0);
            if (l >= 4)
            {
                break;
            }
            af = params;
            j = i1 + l;
            af[j] = af[j] / f;
            l++;
        } while (true);
        while (j < 3) 
        {
            af = pnIndex;
            float f2;
            if (params[i1 + j] >= 0.0F)
            {
                f2 = j + 3;
            } else
            {
                f2 = j;
            }
            af[i * 6 + j] = f2;
            af = pnIndex;
            if (params[i1 + j] >= 0.0F)
            {
                f2 = j;
            } else
            {
                f2 = j + 3;
            }
            af[i * 6 + j + 3] = f2;
            j++;
        }
    }

    private float planeDistance(int i, int j, float af[])
    {
        float f = 0.0F;
        for (int k = 0; k < 3; k++)
        {
            f += params[i + k] * af[pnIndex[j + k]];
        }

        return params[i + 3] + f;
    }

    public int testBoundingBox(float af[], float af1[])
    {
        int i = 0;
        int j = 0;
        int k = 0;
        for (; i < 6; i++)
        {
            if (planeDistance(k, j, af) < 0.0F)
            {
                return -1;
            }
            float f = planeDistance(k, j + 3, af);
            if (i == 0)
            {
                af1[0] = f;
            }
            if (f < 0.0F)
            {
                return 0;
            }
            k += 4;
            j += 6;
        }

        return 1;
    }
}
