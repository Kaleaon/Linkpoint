// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class SLSkeletonBone
{
    private final LLVector3 basePosition;
    public final SLSkeletonBoneID boneID;
    private final int boneIndex;
    private final SLSkeletonBone[] childBones;
    private final SLSkeletonBone[] collisionVolumes;
    private final LLVector3 defaultBasePosition;
    private float globalBaseX;
    private float globalBaseY;
    private float globalBaseZ;
    private final float[] globalMatrix;
    private final LLVector3 offset;
    private SLSkeletonBone parent;
    private final LLVector3 scale;
    private final float[] tempMatrix;
    private final LLVector3 usePosition;
    
    SLSkeletonBone(final SLSkeletonBoneID boneID, LLVector3 basePosition, final LLVector3 llVector3, final SLSkeletonBone[] childBones, final SLSkeletonBone[] collisionVolumes) {
        this.globalMatrix = new float[16];
        this.tempMatrix = new float[16];
        this.boneID = boneID;
        this.boneIndex = boneID.ordinal();
        this.basePosition = new LLVector3(llVector3);
        basePosition = new LLVector3(basePosition);
        this.defaultBasePosition = new LLVector3(this.basePosition);
        this.offset = new LLVector3();
        this.scale = new LLVector3(1.0f, 1.0f, 1.0f);
        this.childBones = childBones;
        this.collisionVolumes = collisionVolumes;
        if (boneID.isJoint) {
            basePosition = this.basePosition;
        }
        this.usePosition = basePosition;
        this.parent = null;
        this.globalBaseX = 0.0f;
        this.globalBaseY = 0.0f;
        this.globalBaseZ = 0.0f;
        if (childBones != null) {
            for (int length = childBones.length, i = 0; i < length; ++i) {
                childBones[i].parent = this;
            }
        }
        if (collisionVolumes != null) {
            for (int length2 = collisionVolumes.length, j = 0; j < length2; ++j) {
                collisionVolumes[j].parent = this;
            }
        }
    }
    
    void deform(final LLVector3 llVector3, final LLVector3 llVector4) {
        this.offset.add(llVector3);
        this.scale.mul(llVector4);
    }
    
    public void deformHierarchy(final LLVector3 llVector3, final LLVector3 llVector4) {
        this.offset.add(llVector3);
        this.scale.mul(llVector4);
        if (this.collisionVolumes != null) {
            final SLSkeletonBone[] collisionVolumes = this.collisionVolumes;
            for (int i = 0; i < collisionVolumes.length; ++i) {
                collisionVolumes[i].deform(llVector3, llVector4);
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
    
    int prepareSkeleton(final SLSkeletonBone[] array, int prepareSkeleton) {
        final int n = 0;
        int prepareSkeleton2 = prepareSkeleton + 1;
        array[prepareSkeleton] = this;
        if (this.parent == null) {
            this.globalBaseX = this.defaultBasePosition.x;
            this.globalBaseY = this.defaultBasePosition.y;
            this.globalBaseZ = this.defaultBasePosition.z;
        }
        else {
            this.globalBaseX = this.parent.globalBaseX + this.defaultBasePosition.x;
            this.globalBaseY = this.parent.globalBaseY + this.defaultBasePosition.y;
            this.globalBaseZ = this.parent.globalBaseZ + this.defaultBasePosition.z;
        }
        prepareSkeleton = prepareSkeleton2;
        if (this.childBones != null) {
            final SLSkeletonBone[] childBones = this.childBones;
            final int length = childBones.length;
            int n2 = 0;
            while (true) {
                prepareSkeleton = prepareSkeleton2;
                if (n2 >= length) {
                    break;
                }
                prepareSkeleton2 = childBones[n2].prepareSkeleton(array, prepareSkeleton2);
                ++n2;
            }
        }
        int n3 = prepareSkeleton;
        if (this.collisionVolumes != null) {
            final SLSkeletonBone[] collisionVolumes = this.collisionVolumes;
            final int length2 = collisionVolumes.length;
            int n4 = n;
            while (true) {
                n3 = prepareSkeleton;
                if (n4 >= length2) {
                    break;
                }
                prepareSkeleton = collisionVolumes[n4].prepareSkeleton(array, prepareSkeleton);
                ++n4;
            }
        }
        return n3;
    }
    
    void setPositionOverride(final LLVector3 llVector3) {
        this.basePosition.set(llVector3);
    }
    
    final void updateGlobalPos(final AnimationSkeletonData animationSkeletonData, final float[] array, final float[] array2) {
        final int animatedIndex = this.boneID.animatedIndex;
        final int n = animatedIndex * 4;
        float n6;
        float n7;
        float n8;
        if (animatedIndex >= 0) {
            final float[] animOffsets = animationSkeletonData.getAnimOffsets();
            final float n2 = animOffsets[n + 3];
            if (n2 > 0.0f) {
                final float n3 = animOffsets[n];
                final float n4 = animOffsets[n + 1];
                final float n5 = animOffsets[n + 2];
                n6 = n2 * n4;
                n7 = n2 * n3;
                n8 = n5 * n2;
            }
            else {
                n8 = 0.0f;
                n6 = 0.0f;
                n7 = 0.0f;
            }
        }
        else {
            n8 = 0.0f;
            n6 = 0.0f;
            n7 = 0.0f;
        }
        if (this.parent != null) {
            Matrix.translateM(this.tempMatrix, 0, this.parent.globalMatrix, 0, n7 + (this.usePosition.x * this.parent.scale.x + this.offset.x), n6 + (this.usePosition.y * this.parent.scale.y + this.offset.y), this.usePosition.z * this.parent.scale.z + this.offset.z + n8);
        }
        else {
            Matrix.setIdentityM(this.tempMatrix, 0);
            Matrix.translateM(this.tempMatrix, 0, this.usePosition.x + this.offset.x + n7, this.usePosition.y + this.offset.y + n6, n8 + (this.usePosition.z + this.offset.z));
        }
        if (animatedIndex >= 0) {
            Matrix.multiplyMM(this.globalMatrix, 0, this.tempMatrix, 0, animationSkeletonData.getAnimMatrix(), animatedIndex * 16);
        }
        else {
            System.arraycopy(this.tempMatrix, 0, this.globalMatrix, 0, 16);
        }
        Matrix.scaleM(array2, this.boneIndex * 16, this.globalMatrix, 0, this.scale.x, this.scale.y, this.scale.z);
        Matrix.translateM(array, this.boneIndex * 16, array2, this.boneIndex * 16, -this.globalBaseX, -this.globalBaseY, -this.globalBaseZ);
    }
}
