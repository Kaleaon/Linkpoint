package com.linkpoint.slproto.avatar

import android.opengl.Matrix
import com.linkpoint.render.avatar.AnimationSkeletonData
import com.linkpoint.slproto.types.LLVector3

class SLSkeletonBone {
    private val LLVector3 basePosition
    val SLSkeletonBoneID boneID
    private val Int boneIndex
    private val SLSkeletonBone[] childBones
    private val SLSkeletonBone[] collisionVolumes
    private val LLVector3 defaultBasePosition
    private Float globalBaseX
    private Float globalBaseY
    private Float globalBaseZ
    private val FloatArray globalMatrix = FloatArray(16)
    private val LLVector3 offset
    private SLSkeletonBone parent
    private val LLVector3 scale
    private val FloatArray tempMatrix = FloatArray(16)
    private val LLVector3 usePosition

    SLSkeletonBone(SLSkeletonBoneID sLSkeletonBoneID, LLVector3 lLVector3, LLVector3 lLVector32, SLSkeletonBone[] sLSkeletonBoneArr, SLSkeletonBone[] sLSkeletonBoneArr2) {
        this.boneID = sLSkeletonBoneID
        this.boneIndex = sLSkeletonBoneID.ordinal()
        this.basePosition = LLVector3(lLVector32)
        LLVector3 lLVector33 = LLVector3(lLVector3)
        this.defaultBasePosition = LLVector3(this.basePosition)
        this.offset = LLVector3()
        this.scale = LLVector3(1.0f, 1.0f, 1.0f)
        this.childBones = sLSkeletonBoneArr
        this.collisionVolumes = sLSkeletonBoneArr2
        this.usePosition = sLSkeletonBoneID.isJoint ? this.basePosition : lLVector33
        this.parent = null
        this.globalBaseX = 0.0f
        this.globalBaseY = 0.0f
        this.globalBaseZ = 0.0f
        if (sLSkeletonBoneArr != null) {
            for (SLSkeletonBone sLSkeletonBone : sLSkeletonBoneArr) {
                sLSkeletonBone.parent = this
            }
        }
        if (sLSkeletonBoneArr2 != null) {
            for (SLSkeletonBone sLSkeletonBone2 : sLSkeletonBoneArr2) {
                sLSkeletonBone2.parent = this
            }
        }
    }

    /* access modifiers changed from: package-private */
    fun deform(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.offset.add(lLVector3)
        this.scale.mul(lLVector32)
    }

    fun deformHierarchy(LLVector3 lLVector3, LLVector3 lLVector32) {
        this.offset.add(lLVector3)
        this.scale.mul(lLVector32)
        if (this.collisionVolumes != null) {
            for (SLSkeletonBone deform : this.collisionVolumes) {
                deform.deform(lLVector3, lLVector32)
            }
        }
    }

    public LLVector3 getBasePosition() {
        return this.basePosition
    }

    public FloatArray getGlobalMatrix() {
        return this.globalMatrix
    }

    public Float getPositionX() {
        return this.basePosition.x + this.offset.x
    }

    public Float getPositionY() {
        return this.basePosition.y + this.offset.y
    }

    public Float getPositionZ() {
        return this.basePosition.z + this.offset.z
    }

    public Float getScaleX() {
        return this.scale.x
    }

    public Float getScaleY() {
        return this.scale.y
    }

    public Float getScaleZ() {
        return this.scale.z
    }

    /* access modifiers changed from: package-private */
    public Int prepareSkeleton(SLSkeletonBone[] sLSkeletonBoneArr, Int i) {
        Int i2 = 0
        Int i3 = i + 1
        sLSkeletonBoneArr[i] = this
        if (this.parent == null) {
            this.globalBaseX = this.defaultBasePosition.x
            this.globalBaseY = this.defaultBasePosition.y
            this.globalBaseZ = this.defaultBasePosition.z
        } else {
            this.globalBaseX = this.parent.globalBaseX + this.defaultBasePosition.x
            this.globalBaseY = this.parent.globalBaseY + this.defaultBasePosition.y
            this.globalBaseZ = this.parent.globalBaseZ + this.defaultBasePosition.z
        }
        if (this.childBones != null) {
            SLSkeletonBone[] sLSkeletonBoneArr2 = this.childBones
            Int length = sLSkeletonBoneArr2.length
            Int i4 = 0
            while (i4 < length) {
                Int prepareSkeleton = sLSkeletonBoneArr2[i4].prepareSkeleton(sLSkeletonBoneArr, i3)
                i4++
                i3 = prepareSkeleton
            }
        }
        if (this.collisionVolumes != null) {
            SLSkeletonBone[] sLSkeletonBoneArr3 = this.collisionVolumes
            Int length2 = sLSkeletonBoneArr3.length
            while (i2 < length2) {
                Int prepareSkeleton2 = sLSkeletonBoneArr3[i2].prepareSkeleton(sLSkeletonBoneArr, i3)
                i2++
                i3 = prepareSkeleton2
            }
        }
        return i3
    }

    /* access modifiers changed from: package-private */
    fun setPositionOverride(LLVector3 lLVector3) {
        this.basePosition.set(lLVector3)
    }

    /* access modifiers changed from: package-private */
    val Unit updateGlobalPos(AnimationSkeletonData animationSkeletonData, FloatArray fArr, FloatArray fArr2) {
        Float f
        Float f2
        Float f3
        Int i = this.boneID.animatedIndex
        Int i2 = i * 4
        Int i3 = i * 16
        if (i >= 0) {
            FloatArray animOffsets = animationSkeletonData.getAnimOffsets()
            Float f4 = animOffsets[i2 + 3]
            if (f4 > 0.0f) {
                Float f5 = f4 * animOffsets[i2]
                f2 = f4 * animOffsets[i2 + 1]
                f3 = f5
                f = animOffsets[i2 + 2] * f4
            } else {
                f = 0.0f
                f2 = 0.0f
                f3 = 0.0f
            }
        } else {
            f = 0.0f
            f2 = 0.0f
            f3 = 0.0f
        }
        if (this.parent != null) {
            Matrix.translateM(this.tempMatrix, 0, this.parent.globalMatrix, 0, f3 + (this.usePosition.x * this.parent.scale.x) + this.offset.x, f2 + (this.usePosition.y * this.parent.scale.y) + this.offset.y, (this.usePosition.z * this.parent.scale.z) + this.offset.z + f)
        } else {
            Matrix.setIdentityM(this.tempMatrix, 0)
            Matrix.translateM(this.tempMatrix, 0, this.usePosition.x + this.offset.x + f3, this.usePosition.y + this.offset.y + f2, f + this.usePosition.z + this.offset.z)
        }
        if (i >= 0) {
            Matrix.multiplyMM(this.globalMatrix, 0, this.tempMatrix, 0, animationSkeletonData.getAnimMatrix(), i3)
        } else {
            System.arraycopy(this.tempMatrix, 0, this.globalMatrix, 0, 16)
        }
        Matrix.scaleM(fArr2, this.boneIndex * 16, this.globalMatrix, 0, this.scale.x, this.scale.y, this.scale.z)
        Matrix.translateM(fArr, this.boneIndex * 16, fArr2, this.boneIndex * 16, -this.globalBaseX, -this.globalBaseY, -this.globalBaseZ)
    }
}
