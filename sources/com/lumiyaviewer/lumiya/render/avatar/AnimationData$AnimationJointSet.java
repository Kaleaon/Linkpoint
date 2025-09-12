// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.util.SparseArray;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AnimationData, AnimationTiming, AvatarSkeleton

static class <init>
{

    private final float animLength;
    private final UUID animationUUID;
    private final SparseArray jointAnims;
    private final int priority;

    void addJointData(int i,  )
    {
        jointAnims.put(i, );
    }

    void animate(AvatarSkeleton avatarskeleton, AnimationTiming animationtiming, float af[], float af1[], LLQuaternion allquaternion[], LLVector3 allvector3[])
    {
        float f = animationtiming.inAnimationTime;
        float f1 = animationtiming.inFactor * animationtiming.outFactor;
        if (f1 > 0.0F)
        {
            animationtiming = new LLQuaternion();
            LLVector3 llvector3 = new LLVector3();
            int j = jointAnims.size();
            for (int i = 0; i < j; i++)
            {
                int k = jointAnims.keyAt(i);
                (()jointAnims.valueAt(i)).animate(avatarskeleton.getAnimatedBone(k), animLength, f, allquaternion, allvector3, k, f1, af, af1, animationtiming, llvector3);
            }

        }
    }

    void dumpJoints()
    {
        Debug.Printf("Anim -- joint set -- length %f prio %d joints %d", new Object[] {
            Float.valueOf(animLength), Integer.valueOf(priority), Integer.valueOf(jointAnims.size())
        });
        int j = jointAnims.size();
        for (int i = 0; i < j; i++)
        {
            Debug.Printf("Anim -- joint[%d] - jointIndex %d, %s", new Object[] {
                Integer.valueOf(i), Integer.valueOf(jointAnims.keyAt(i)), (()jointAnims.valueAt(i)).toString()
            });
        }

    }

    public int getPriority()
    {
        return priority;
    }

    private (UUID uuid, float f, int i)
    {
        jointAnims = new SparseArray();
        animationUUID = uuid;
        animLength = f;
        priority = i;
    }

    priority(UUID uuid, float f, int i, priority priority1)
    {
        this(uuid, f, i);
    }
}
