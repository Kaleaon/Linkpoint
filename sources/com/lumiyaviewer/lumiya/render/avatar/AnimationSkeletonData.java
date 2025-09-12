// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Arrays;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarAnimationList, AvatarSkeleton

public class AnimationSkeletonData
{

    private static final int numAnimatedBones = 133;
    private float animMatrix[];
    private float animMatrix_Swap[];
    private float animOffsets[];
    private float animOffsets_Swap[];
    private final LLVector3 animPosArray[] = new LLVector3[133];
    private final float animPriorityPosArray[] = new float[133];
    private final float animPriorityRotArray[] = new float[133];
    private final LLQuaternion animRotArray[] = new LLQuaternion[133];

    AnimationSkeletonData()
    {
        animMatrix = new float[2128];
        animMatrix_Swap = new float[2128];
        animOffsets = new float[532];
        animOffsets_Swap = new float[532];
        for (int i = 0; i < 133; i++)
        {
            Matrix.setIdentityM(animMatrix, i * 16);
            animPosArray[i] = new LLVector3();
            animRotArray[i] = new LLQuaternion();
        }

        Arrays.fill(animOffsets, 0.0F);
    }

    void animate(AvatarSkeleton avatarskeleton, AvatarAnimationList avataranimationlist)
    {
        Arrays.fill(animPriorityRotArray, 1.0F);
        Arrays.fill(animPriorityPosArray, 1.0F);
        for (int i = 0; i < 133; i++)
        {
            animRotArray[i].setZero();
            animPosArray[i].set(0.0F, 0.0F, 0.0F);
        }

        avataranimationlist.animate(avatarskeleton, animPriorityRotArray, animPriorityPosArray, animRotArray, animPosArray);
        for (int j = 0; j < 133; j++)
        {
            animRotArray[j].getInverseMatrix(animMatrix_Swap, j * 16);
            animOffsets_Swap[j * 4 + 0] = animPosArray[j].x;
            animOffsets_Swap[j * 4 + 1] = animPosArray[j].y;
            animOffsets_Swap[j * 4 + 2] = animPosArray[j].z;
            animOffsets_Swap[j * 4 + 3] = 1.0F - animPriorityPosArray[j];
        }

        avatarskeleton = animMatrix;
        animMatrix = animMatrix_Swap;
        animMatrix_Swap = avatarskeleton;
        avatarskeleton = animOffsets;
        animOffsets = animOffsets_Swap;
        animOffsets_Swap = avatarskeleton;
    }

    public final float[] getAnimMatrix()
    {
        return animMatrix;
    }

    public final float[] getAnimOffsets()
    {
        return animOffsets;
    }
}
