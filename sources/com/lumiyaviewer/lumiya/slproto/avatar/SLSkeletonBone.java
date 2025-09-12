// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLSkeletonBoneID

public class SLSkeletonBone
{

    private final LLVector3 basePosition;
    public final SLSkeletonBoneID boneID;
    private final int boneIndex;
    private final SLSkeletonBone childBones[];
    private final SLSkeletonBone collisionVolumes[];
    private final LLVector3 defaultBasePosition;
    private float globalBaseX;
    private float globalBaseY;
    private float globalBaseZ;
    private final float globalMatrix[] = new float[16];
    private final LLVector3 offset = new LLVector3();
    private SLSkeletonBone parent;
    private final LLVector3 scale = new LLVector3(1.0F, 1.0F, 1.0F);
    private final float tempMatrix[] = new float[16];
    private final LLVector3 usePosition;

    SLSkeletonBone(SLSkeletonBoneID slskeletonboneid, LLVector3 llvector3, LLVector3 llvector3_1, SLSkeletonBone aslskeletonbone[], SLSkeletonBone aslskeletonbone1[])
    {
        boneID = slskeletonboneid;
        boneIndex = slskeletonboneid.ordinal();
        basePosition = new LLVector3(llvector3_1);
        llvector3 = new LLVector3(llvector3);
        defaultBasePosition = new LLVector3(basePosition);
        childBones = aslskeletonbone;
        collisionVolumes = aslskeletonbone1;
        if (slskeletonboneid.isJoint)
        {
            llvector3 = basePosition;
        }
        usePosition = llvector3;
        parent = null;
        globalBaseX = 0.0F;
        globalBaseY = 0.0F;
        globalBaseZ = 0.0F;
        if (aslskeletonbone != null)
        {
            int k = aslskeletonbone.length;
            for (int i = 0; i < k; i++)
            {
                aslskeletonbone[i].parent = this;
            }

        }
        if (aslskeletonbone1 != null)
        {
            int l = aslskeletonbone1.length;
            for (int j = 0; j < l; j++)
            {
                aslskeletonbone1[j].parent = this;
            }

        }
    }

    void deform(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        offset.add(llvector3);
        scale.mul(llvector3_1);
    }

    public void deformHierarchy(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        offset.add(llvector3);
        scale.mul(llvector3_1);
        if (collisionVolumes != null)
        {
            SLSkeletonBone aslskeletonbone[] = collisionVolumes;
            int i = 0;
            for (int j = aslskeletonbone.length; i < j; i++)
            {
                aslskeletonbone[i].deform(llvector3, llvector3_1);
            }

        }
    }

    public LLVector3 getBasePosition()
    {
        return basePosition;
    }

    public float[] getGlobalMatrix()
    {
        return globalMatrix;
    }

    public float getPositionX()
    {
        return basePosition.x + offset.x;
    }

    public float getPositionY()
    {
        return basePosition.y + offset.y;
    }

    public float getPositionZ()
    {
        return basePosition.z + offset.z;
    }

    public float getScaleX()
    {
        return scale.x;
    }

    public float getScaleY()
    {
        return scale.y;
    }

    public float getScaleZ()
    {
        return scale.z;
    }

    int prepareSkeleton(SLSkeletonBone aslskeletonbone[], int i)
    {
        boolean flag = false;
        int j = i + 1;
        aslskeletonbone[i] = this;
        if (parent == null)
        {
            globalBaseX = defaultBasePosition.x;
            globalBaseY = defaultBasePosition.y;
            globalBaseZ = defaultBasePosition.z;
        } else
        {
            globalBaseX = parent.globalBaseX + defaultBasePosition.x;
            globalBaseY = parent.globalBaseY + defaultBasePosition.y;
            globalBaseZ = parent.globalBaseZ + defaultBasePosition.z;
        }
        i = j;
        if (childBones != null)
        {
            SLSkeletonBone aslskeletonbone1[] = childBones;
            int j1 = aslskeletonbone1.length;
            int l = 0;
            do
            {
                i = j;
                if (l >= j1)
                {
                    break;
                }
                j = aslskeletonbone1[l].prepareSkeleton(aslskeletonbone, j);
                l++;
            } while (true);
        }
        int i1 = i;
        if (collisionVolumes != null)
        {
            SLSkeletonBone aslskeletonbone2[] = collisionVolumes;
            int k1 = aslskeletonbone2.length;
            int k = ((flag) ? 1 : 0);
            do
            {
                i1 = i;
                if (k >= k1)
                {
                    break;
                }
                i = aslskeletonbone2[k].prepareSkeleton(aslskeletonbone, i);
                k++;
            } while (true);
        }
        return i1;
    }

    void setPositionOverride(LLVector3 llvector3)
    {
        basePosition.set(llvector3);
    }

    final void updateGlobalPos(AnimationSkeletonData animationskeletondata, float af[], float af1[])
    {
        int i = boneID.animatedIndex;
        int j = i * 4;
        float f;
        float f1;
        float f2;
        if (i >= 0)
        {
            float af2[] = animationskeletondata.getAnimOffsets();
            f = af2[j + 3];
            if (f > 0.0F)
            {
                f1 = af2[j];
                f2 = af2[j + 1];
                float f3 = af2[j + 2];
                f2 = f * f2;
                f1 = f * f1;
                f = f3 * f;
            } else
            {
                f = 0.0F;
                f2 = 0.0F;
                f1 = 0.0F;
            }
        } else
        {
            f = 0.0F;
            f2 = 0.0F;
            f1 = 0.0F;
        }
        if (parent != null)
        {
            Matrix.translateM(tempMatrix, 0, parent.globalMatrix, 0, f1 + (usePosition.x * parent.scale.x + offset.x), f2 + (usePosition.y * parent.scale.y + offset.y), usePosition.z * parent.scale.z + offset.z + f);
        } else
        {
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, usePosition.x + offset.x + f1, usePosition.y + offset.y + f2, f + (usePosition.z + offset.z));
        }
        if (i >= 0)
        {
            Matrix.multiplyMM(globalMatrix, 0, tempMatrix, 0, animationskeletondata.getAnimMatrix(), i * 16);
        } else
        {
            System.arraycopy(tempMatrix, 0, globalMatrix, 0, 16);
        }
        Matrix.scaleM(af1, boneIndex * 16, globalMatrix, 0, scale.x, scale.y, scale.z);
        Matrix.translateM(af, boneIndex * 16, af1, boneIndex * 16, -globalBaseX, -globalBaseY, -globalBaseZ);
    }
}
