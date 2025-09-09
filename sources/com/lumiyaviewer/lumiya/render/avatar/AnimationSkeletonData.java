package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Arrays;

public class AnimationSkeletonData {
    private static final int numAnimatedBones = 133;
    private float[] animMatrix = new float[2128];
    private float[] animMatrix_Swap = new float[2128];
    private float[] animOffsets = new float[532];
    private float[] animOffsets_Swap = new float[532];
    private final LLVector3[] animPosArray = new LLVector3[133];
    private final float[] animPriorityPosArray = new float[133];
    private final float[] animPriorityRotArray = new float[133];
    private final LLQuaternion[] animRotArray = new LLQuaternion[133];

    AnimationSkeletonData() {
        for (int i = 0; i < 133; i++) {
            Matrix.setIdentityM(this.animMatrix, i * 16);
            this.animPosArray[i] = new LLVector3();
            this.animRotArray[i] = new LLQuaternion();
        }
        Arrays.fill(this.animOffsets, 0.0f);
    }

    /* access modifiers changed from: package-private */
    public void animate(AvatarSkeleton avatarSkeleton, AvatarAnimationList avatarAnimationList) {
        Arrays.fill(this.animPriorityRotArray, 1.0f);
        Arrays.fill(this.animPriorityPosArray, 1.0f);
        for (int i = 0; i < 133; i++) {
            this.animRotArray[i].setZero();
            this.animPosArray[i].set(0.0f, 0.0f, 0.0f);
        }
        avatarAnimationList.animate(avatarSkeleton, this.animPriorityRotArray, this.animPriorityPosArray, this.animRotArray, this.animPosArray);
        for (int i2 = 0; i2 < 133; i2++) {
            this.animRotArray[i2].getInverseMatrix(this.animMatrix_Swap, i2 * 16);
            this.animOffsets_Swap[(i2 * 4) + 0] = this.animPosArray[i2].x;
            this.animOffsets_Swap[(i2 * 4) + 1] = this.animPosArray[i2].y;
            this.animOffsets_Swap[(i2 * 4) + 2] = this.animPosArray[i2].z;
            this.animOffsets_Swap[(i2 * 4) + 3] = 1.0f - this.animPriorityPosArray[i2];
        }
        float[] fArr = this.animMatrix;
        this.animMatrix = this.animMatrix_Swap;
        this.animMatrix_Swap = fArr;
        float[] fArr2 = this.animOffsets;
        this.animOffsets = this.animOffsets_Swap;
        this.animOffsets_Swap = fArr2;
    }

    public final float[] getAnimMatrix() {
        return this.animMatrix;
    }

    public final float[] getAnimOffsets() {
        return this.animOffsets;
    }
}
