package com.lumiyaviewer.lumiya.slproto.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class SLSkeletonBone {
    private final LLVector3 basePosition;
    public final SLSkeletonBoneID boneID;
    private final int boneIndex;
    private final SLSkeletonBone[] childBones;
    private final SLSkeletonBone[] collisionVolumes;
    private final LLVector3 defaultBasePosition;
    private float globalBaseX;
    private float globalBaseY;
    private float globalBaseZ;
    private final float[] globalMatrix = new float[16];
    private final LLVector3 offset;
    private SLSkeletonBone parent;
    private final LLVector3 scale;
    private final float[] tempMatrix = new float[16];
    private final LLVector3 usePosition;

    SLSkeletonBone(SLSkeletonBoneID sLSkeletonBoneID, LLVector3 lLVector3, LLVector3 lLVector32, SLSkeletonBone[] sLSkeletonBoneArr, SLSkeletonBone[] sLSkeletonBoneArr2) {
        this.boneID = sLSkeletonBoneID;
        this.boneIndex = sLSkeletonBoneID.ordinal();
        this.basePosition = new LLVector3(lLVector32);
        LLVector3 lLVector33 = new LLVector3(lLVector3);
        this.defaultBasePosition = new LLVector3(this.basePosition);
        this.offset = new LLVector3();
        this.scale = new LLVector3(1.0f, 1.0f, 1.0f);
        this.childBones = sLSkeletonBoneArr;
        this.collisionVolumes = sLSkeletonBoneArr2;
        this.usePosition = sLSkeletonBoneID.isJoint ? this.basePosition : lLVector33;
        this.parent = null;
        this.globalBaseX = 0.0f;
        this.globalBaseY = 0.0f;
        this.globalBaseZ = 0.0f;
        if (sLSkeletonBoneArr != null) {
            for (SLSkeletonBone sLSkeletonBone : sLSkeletonBoneArr) {
                sLSkeletonBone.parent = this;
            }
        }
        if (sLSkeletonBoneArr2 != null) {
            for (SLSkeletonBone sLSkeletonBone2 : sLSkeletonBoneArr2) {
                sLSkeletonBone2.parent = this;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void deform(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.offset.add(lLVector3);
        this.scale.mul(lLVector32);
    }

    public void deformHierarchy(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.offset.add(lLVector3);
        this.scale.mul(lLVector32);
        if (this.collisionVolumes != null) {
            for (SLSkeletonBone deform : this.collisionVolumes) {
                deform.deform(lLVector3, lLVector32);
            }
        }
    }

    public LLVector3 getBasePosition() {
        return this.basePosition;
    }

    public float[] getGlobalMatrix() {
        return this.globalMatrix;
    }

    public float getPositionX() {
        return this.basePosition.x + this.offset.x;
    }

    public float getPositionY() {
        return this.basePosition.y + this.offset.y;
    }

    public float getPositionZ() {
        return this.basePosition.z + this.offset.z;
    }

    public float getScaleX() {
        return this.scale.x;
    }

    public float getScaleY() {
        return this.scale.y;
    }

    public float getScaleZ() {
        return this.scale.z;
    }

    /* access modifiers changed from: package-private */
    public int prepareSkeleton(SLSkeletonBone[] sLSkeletonBoneArr, int i) {
        int i2 = 0;
        int i3 = i + 1;
        sLSkeletonBoneArr[i] = this;
        if (this.parent == null) {
            this.globalBaseX = this.defaultBasePosition.x;
            this.globalBaseY = this.defaultBasePosition.y;
            this.globalBaseZ = this.defaultBasePosition.z;
        } else {
            this.globalBaseX = this.parent.globalBaseX + this.defaultBasePosition.x;
            this.globalBaseY = this.parent.globalBaseY + this.defaultBasePosition.y;
            this.globalBaseZ = this.parent.globalBaseZ + this.defaultBasePosition.z;
        }
        if (this.childBones != null) {
            SLSkeletonBone[] sLSkeletonBoneArr2 = this.childBones;
            int length = sLSkeletonBoneArr2.length;
            int i4 = 0;
            while (i4 < length) {
                int prepareSkeleton = sLSkeletonBoneArr2[i4].prepareSkeleton(sLSkeletonBoneArr, i3);
                i4++;
                i3 = prepareSkeleton;
            }
        }
        if (this.collisionVolumes != null) {
            SLSkeletonBone[] sLSkeletonBoneArr3 = this.collisionVolumes;
            int length2 = sLSkeletonBoneArr3.length;
            while (i2 < length2) {
                int prepareSkeleton2 = sLSkeletonBoneArr3[i2].prepareSkeleton(sLSkeletonBoneArr, i3);
                i2++;
                i3 = prepareSkeleton2;
            }
        }
        return i3;
    }

    /* access modifiers changed from: package-private */
    public void setPositionOverride(LLVector3 lLVector3) {
        this.basePosition.set(lLVector3);
    }

    /* access modifiers changed from: package-private */
    public final void updateGlobalPos(AnimationSkeletonData animationSkeletonData, float[] fArr, float[] fArr2) {
        float f;
        float f2;
        float f3;
        int i = this.boneID.animatedIndex;
        int i2 = i * 4;
        int i3 = i * 16;
        if (i >= 0) {
            float[] animOffsets = animationSkeletonData.getAnimOffsets();
            float f4 = animOffsets[i2 + 3];
            if (f4 > 0.0f) {
                float f5 = f4 * animOffsets[i2];
                f2 = f4 * animOffsets[i2 + 1];
                f3 = f5;
                f = animOffsets[i2 + 2] * f4;
            } else {
                f = 0.0f;
                f2 = 0.0f;
                f3 = 0.0f;
            }
        } else {
            f = 0.0f;
            f2 = 0.0f;
            f3 = 0.0f;
        }
        if (this.parent != null) {
            Matrix.translateM(this.tempMatrix, 0, this.parent.globalMatrix, 0, f3 + (this.usePosition.x * this.parent.scale.x) + this.offset.x, f2 + (this.usePosition.y * this.parent.scale.y) + this.offset.y, (this.usePosition.z * this.parent.scale.z) + this.offset.z + f);
        } else {
            Matrix.setIdentityM(this.tempMatrix, 0);
            Matrix.translateM(this.tempMatrix, 0, this.usePosition.x + this.offset.x + f3, this.usePosition.y + this.offset.y + f2, f + this.usePosition.z + this.offset.z);
        }
        if (i >= 0) {
            Matrix.multiplyMM(this.globalMatrix, 0, this.tempMatrix, 0, animationSkeletonData.getAnimMatrix(), i3);
        } else {
            System.arraycopy(this.tempMatrix, 0, this.globalMatrix, 0, 16);
        }
        Matrix.scaleM(fArr2, this.boneIndex * 16, this.globalMatrix, 0, this.scale.x, this.scale.y, this.scale.z);
        Matrix.translateM(fArr, this.boneIndex * 16, fArr2, this.boneIndex * 16, -this.globalBaseX, -this.globalBaseY, -this.globalBaseZ);
    }
}
