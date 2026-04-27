// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListEntry

public abstract class DrawListObjectEntry extends DrawListEntry
{

    final SLObjectInfo objectInfo;

    public DrawListObjectEntry(SLObjectInfo slobjectinfo)
    {
        objectInfo = slobjectinfo;
    }

    public SLObjectInfo getObjectInfo()
    {
        return objectInfo;
    }

    public void updateBoundingBox()
    {
        float af[] = objectInfo.worldMatrix;
        if (af != null)
        {
            Vector3Array vector3array = objectInfo.getObjectCoords();
            float af1[] = vector3array.getData();
            int i1 = vector3array.getElementOffset(1);
            for (int i = 0; i < 3; i++)
            {
                float af2[] = boundingBox;
                float f = af[i + 12];
                boundingBox[i + 3] = f;
                af2[i] = f;
            }

            for (int j = 0; j < 3; j++)
            {
                int l = 0;
                while (l < 3) 
                {
                    float f1 = af[j * 4 + l] * (-af1[i1 + l] / 2.0F);
                    float f3 = af[j * 4 + l] * (af1[i1 + l] / 2.0F);
                    if (f1 < f3)
                    {
                        float af3[] = boundingBox;
                        af3[j] = f1 + af3[j];
                        af3 = boundingBox;
                        int j1 = j + 3;
                        af3[j1] = f3 + af3[j1];
                    } else
                    {
                        float af4[] = boundingBox;
                        af4[j] = f3 + af4[j];
                        af4 = boundingBox;
                        int k1 = j + 3;
                        af4[k1] = f1 + af4[k1];
                    }
                    l++;
                }
            }

            int k = 0;
            while (k < 3) 
            {
                float f2;
                if (k == 2)
                {
                    f2 = 4096F;
                } else
                {
                    f2 = 256F;
                }
                boundingBox[k] = Math.min(f2, Math.max(0.0F, boundingBox[k]));
                boundingBox[k + 3] = Math.min(f2, Math.max(0.0F, boundingBox[k + 3]));
                k++;
            }
        }
    }
}
