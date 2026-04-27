// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;

import android.opengl.Matrix;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            VectorArray, LLVector3, LLQuaternion

public class Vector3Array extends VectorArray
{

    public Vector3Array(int i)
    {
        super(3, i);
    }

    public Vector3Array(VectorArray vectorarray, int i)
    {
        super(vectorarray, i);
    }

    public final void MatrixScale(float af[], int i, int j)
    {
        j = offset + numComponents * j;
        Matrix.scaleM(af, i, data[j + 0], data[j + 1], data[j + 2]);
    }

    public final void MatrixTranslate(float af[], int i, float af1[], int j, int k)
    {
        k = offset + numComponents * k;
        Matrix.translateM(af, i, af1, j, data[k + 0], data[k + 1], data[k + 2]);
    }

    public final void add(int i, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        float af[] = data;
        int j = i + 0;
        af[j] = af[j] + llvector3.x;
        af = data;
        j = i + 1;
        af[j] = af[j] + llvector3.y;
        af = data;
        i += 2;
        af[i] = af[i] + llvector3.z;
    }

    public final void addToVector(int i, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        llvector3.x = llvector3.x + data[i + 0];
        llvector3.y = llvector3.y + data[i + 1];
        float f = llvector3.z;
        llvector3.z = data[i + 2] + f;
    }

    public final void clear()
    {
        int j = offset;
        for (int i = 0; i < length; i++)
        {
            data[j + 0] = 0.0F;
            data[j + 1] = 0.0F;
            data[j + 2] = 0.0F;
            j += numComponents;
        }

    }

    public final float distToPlane(int i, LLVector3 llvector3, LLVector3 llvector3_1)
    {
        i = offset + numComponents * i;
        float f = data[i + 0];
        float f1 = llvector3.x;
        float f2 = data[i + 1];
        float f3 = llvector3.y;
        float f4 = data[i + 2];
        float f5 = llvector3.z;
        float f6 = llvector3_1.x;
        float f7 = llvector3_1.y;
        return (f4 - f5) * llvector3_1.z + ((f - f1) * f6 + (f2 - f3) * f7);
    }

    public final void fill(int i, int j, LLVector3 llvector3)
    {
        int k = offset;
        k = numComponents * i + k;
        for (i = 0; i < j; i++)
        {
            data[k + 0] = llvector3.x;
            data[k + 1] = llvector3.y;
            data[k + 2] = llvector3.z;
            k += numComponents;
        }

    }

    public final LLVector3 get(int i)
    {
        i = offset + numComponents * i;
        return new LLVector3(data[i + 0], data[i + 1], data[i + 2]);
    }

    public final void get(int i, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        llvector3.x = data[i + 0];
        llvector3.y = data[i + 1];
        llvector3.z = data[i + 2];
    }

    public final float getDistanceTo(int i, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        float f = data[i + 0] - llvector3.x;
        float f1 = data[i + 1] - llvector3.y;
        float f2 = data[i + 2] - llvector3.z;
        return (float)Math.sqrt(f2 * f2 + (f * f + f1 * f1));
    }

    public final float getMaxComponent(int i)
    {
        int j = offset;
        i = numComponents * i + j;
        float f1 = data[i + 0];
        float f = f1;
        if (data[i + 1] > f1)
        {
            f = data[i + 1];
        }
        f1 = f;
        if (data[i + 2] > f)
        {
            f1 = data[i + 2];
        }
        return f1;
    }

    public final void getSub(int i, int j, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        j = offset + numComponents * j;
        llvector3.x = data[i + 0] - data[j + 0];
        llvector3.y = data[i + 1] - data[j + 1];
        llvector3.z = data[i + 2] - data[j + 2];
    }

    public final void getSub(int i, Vector3Array vector3array, int j, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        j = vector3array.offset + vector3array.numComponents * j;
        llvector3.x = data[i + 0] - vector3array.data[j + 0];
        llvector3.y = data[i + 1] - vector3array.data[j + 1];
        llvector3.z = data[i + 2] - vector3array.data[j + 2];
    }

    public final void minMaxVector(int i, LLVector3 llvector3, LLVector3 llvector3_1)
    {
        i = offset + numComponents * i;
        float f = data[i + 0];
        float f1 = data[i + 1];
        float f2 = data[i + 2];
        if (llvector3.x > f)
        {
            llvector3.x = f;
        }
        if (llvector3_1.x < f)
        {
            llvector3_1.x = f;
        }
        if (llvector3.y > f1)
        {
            llvector3.y = f1;
        }
        if (llvector3_1.y < f1)
        {
            llvector3_1.y = f1;
        }
        if (llvector3.z > f2)
        {
            llvector3.z = f2;
        }
        if (llvector3_1.z < f2)
        {
            llvector3_1.z = f2;
        }
    }

    public final void minMaxVector(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        int j = offset;
        for (int i = 0; i < length; i++)
        {
            float f = data[j + 0];
            float f1 = data[j + 1];
            float f2 = data[j + 2];
            if (llvector3.x > f)
            {
                llvector3.x = f;
            }
            if (llvector3_1.x < f)
            {
                llvector3_1.x = f;
            }
            if (llvector3.y > f1)
            {
                llvector3.y = f1;
            }
            if (llvector3_1.y < f1)
            {
                llvector3_1.y = f1;
            }
            if (llvector3.z > f2)
            {
                llvector3.z = f2;
            }
            if (llvector3_1.z < f2)
            {
                llvector3_1.z = f2;
            }
            j += numComponents;
        }

    }

    public final void mul(int i, LLQuaternion llquaternion)
    {
        i = offset + numComponents * i;
        float f3 = data[i + 0];
        float f4 = data[i + 1];
        float f5 = data[i + 2];
        float f2 = -llquaternion.x * f3 - llquaternion.y * f4 - llquaternion.z * f5;
        float f = (llquaternion.w * f3 + llquaternion.y * f5) - llquaternion.z * f4;
        float f1 = (llquaternion.w * f4 + llquaternion.z * f3) - llquaternion.x * f5;
        float f6 = llquaternion.w;
        f3 = (f4 * llquaternion.x + f5 * f6) - f3 * llquaternion.y;
        data[i + 0] = ((-f2 * llquaternion.x + llquaternion.w * f) - llquaternion.z * f1) + llquaternion.y * f3;
        data[i + 1] = ((-f2 * llquaternion.y + llquaternion.w * f1) - llquaternion.x * f3) + llquaternion.z * f;
        float af[] = data;
        f2 = -f2;
        f4 = llquaternion.z;
        af[i + 2] = ((f3 * llquaternion.w + f2 * f4) - llquaternion.y * f) + llquaternion.x * f1;
    }

    public final void set(int i, float f, float f1, float f2)
    {
        i = offset + numComponents * i;
        data[i + 0] = f;
        data[i + 1] = f1;
        data[i + 2] = f2;
    }

    public final void set(int i, LLVector3 llvector3)
    {
        i = offset + numComponents * i;
        data[i + 0] = llvector3.x;
        data[i + 1] = llvector3.y;
        data[i + 2] = llvector3.z;
    }

    public final void set(int i, Vector3Array vector3array, int j)
    {
        i = offset + numComponents * i;
        j = vector3array.offset + vector3array.numComponents * j;
        data[i + 0] = vector3array.data[j + 0];
        data[i + 1] = vector3array.data[j + 1];
        data[i + 2] = vector3array.data[j + 2];
    }

    public final void setAdd(int i, int j)
    {
        int k = offset;
        k = numComponents * i + k;
        i = offset;
        j = numComponents * j + i;
        for (i = 0; i < 3; i++)
        {
            float af[] = data;
            int l = k + i;
            af[l] = af[l] + data[j + i];
            data[j + i] = data[k + i];
        }

    }

    public final void subFromVector(LLVector3 llvector3, int i)
    {
        i = offset + numComponents * i;
        llvector3.x = llvector3.x - data[i + 0];
        llvector3.y = llvector3.y - data[i + 1];
        llvector3.z = llvector3.z - data[i + 2];
    }
}
