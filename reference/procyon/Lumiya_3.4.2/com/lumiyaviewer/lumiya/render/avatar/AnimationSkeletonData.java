// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import java.util.Arrays;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class AnimationSkeletonData
{
    private static final int numAnimatedBones = 133;
    private float[] animMatrix;
    private float[] animMatrix_Swap;
    private float[] animOffsets;
    private float[] animOffsets_Swap;
    private final LLVector3[] animPosArray;
    private final float[] animPriorityPosArray;
    private final float[] animPriorityRotArray;
    private final LLQuaternion[] animRotArray;
    
    AnimationSkeletonData() {
        this.animPriorityRotArray = new float[133];
        this.animPriorityPosArray = new float[133];
        this.animRotArray = new LLQuaternion[133];
        this.animPosArray = new LLVector3[133];
        this.animMatrix = new float[2128];
        this.animMatrix_Swap = new float[2128];
        this.animOffsets = new float[532];
        this.animOffsets_Swap = new float[532];
        for (int i = 0; i < 133; ++i) {
            Matrix.setIdentityM(this.animMatrix, i * 16);
            this.animPosArray[i] = new LLVector3();
            this.animRotArray[i] = new LLQuaternion();
        }
        Arrays.fill(this.animOffsets, 0.0f);
    }
    
    void animate(final AvatarSkeleton avatarSkeleton, final AvatarAnimationList list) {
        Arrays.fill(this.animPriorityRotArray, 1.0f);
        Arrays.fill(this.animPriorityPosArray, 1.0f);
        for (int i = 0; i < 133; ++i) {
            this.animRotArray[i].setZero();
            this.animPosArray[i].set(0.0f, 0.0f, 0.0f);
        }
        list.animate(avatarSkeleton, this.animPriorityRotArray, this.animPriorityPosArray, this.animRotArray, this.animPosArray);
        for (int j = 0; j < 133; ++j) {
            this.animRotArray[j].getInverseMatrix(this.animMatrix_Swap, j * 16);
            this.animOffsets_Swap[j * 4 + 0] = this.animPosArray[j].x;
            this.animOffsets_Swap[j * 4 + 1] = this.animPosArray[j].y;
            this.animOffsets_Swap[j * 4 + 2] = this.animPosArray[j].z;
            this.animOffsets_Swap[j * 4 + 3] = 1.0f - this.animPriorityPosArray[j];
        }
        final float[] animMatrix = this.animMatrix;
        this.animMatrix = this.animMatrix_Swap;
        this.animMatrix_Swap = animMatrix;
        final float[] animOffsets = this.animOffsets;
        this.animOffsets = this.animOffsets_Swap;
        this.animOffsets_Swap = animOffsets;
    }
    
    public final float[] getAnimMatrix() {
        return this.animMatrix;
    }
    
    public final float[] getAnimOffsets() {
        return this.animOffsets;
    }
}
