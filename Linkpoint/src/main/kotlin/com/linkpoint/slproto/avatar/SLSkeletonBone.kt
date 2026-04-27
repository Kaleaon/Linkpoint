package com.linkpoint.slproto.avatar

import android.opengl.Matrix
import com.linkpoint.render.avatar.AnimationSkeletonData
import com.linkpoint.slproto.types.LLVector3

class SLSkeletonBone {
    private val LLVector3 basePosition
    val SLSkeletonBoneID boneID
    private val Int boneIndex
    private val Array<SLSkeletonBone> childBones
    private val Array<SLSkeletonBone> collisionVolumes
    private val LLVector3 defaultBasePosition
    private Float globalBaseX
    private Float globalBaseY
    private Float globalBaseZ
    private val FloatArray globalMatrix = Float[16]
    private val LLVector3 offset
    private SLSkeletonBone parent
    private val LLVector3 scale
    private val FloatArray tempMatrix = Float[16]
    private val LLVector3 usePosition

    SLSkeletonBone(SLSkeletonBoneID sLSkeletonBoneID, LLVector3 lLVector3, LLVector3 lLVector32, Array<SLSkeletonBone> sLSkeletonBoneArr, Array<SLSkeletonBone> sLSkeletonBoneArr2) {
        this.boneID = sLSkeletonBoneID
        this.boneIndex = sLSkeletonBoneID.ordinal()
        this.basePosition = LLVector3(lLVector32)
        val lLVector33: LLVector3 = LLVector3(lLVector3)
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
    fun deform(lLVector3: LLVector3, lLVector32: LLVector3) {
        this.offset.add(lLVector3)
        this.scale.mul(lLVector32)
    }

    fun deformHierarchy(lLVector3: LLVector3, lLVector32: LLVector3) {
        this.offset.add(lLVector3)
        this.scale.mul(lLVector32)
        if (this.collisionVolumes != null) {
            for (SLSkeletonBone deform : this.collisionVolumes) {
                deform.deform(lLVector3, lLVector32)
            }
        }
    }

     public fun getBasePosition(): LLVector3 {
        return this.basePosition
    }

     public fun getGlobalMatrix(): FloatArray {
        return this.globalMatrix
    }

     public fun getPositionX(): Float {
        return this.basePosition.x + this.offset.x
    }

     public fun getPositionY(): Float {
        return this.basePosition.y + this.offset.y
    }

     public fun getPositionZ(): Float {
        return this.basePosition.z + this.offset.z
    }

     public fun getScaleX(): Float {
        return this.scale.x
    }

     public fun getScaleY(): Float {
        return this.scale.y
    }

     public fun getScaleZ(): Float {
        return this.scale.z
    }

    /* access modifiers changed from: package-private */
     public fun prepareSkeleton(sLSkeletonBoneArr: Array<SLSkeletonBone>, i: Int): Int {
        val i2: Int = 0
        val i3: Int = i + 1
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
            val sLSkeletonBoneArr2: Array<SLSkeletonBone> = this.childBones
            val length: Int = sLSkeletonBoneArr2.length
            val i4: Int = 0
            while (i4 < length) {
                val prepareSkeleton: Int = sLSkeletonBoneArr2[i4].prepareSkeleton(sLSkeletonBoneArr, i3)
                i4++
                i3 = prepareSkeleton
            }
        }
        if (this.collisionVolumes != null) {
            val sLSkeletonBoneArr3: Array<SLSkeletonBone> = this.collisionVolumes
            val length2: Int = sLSkeletonBoneArr3.length
            while (i2 < length2) {
                val prepareSkeleton2: Int = sLSkeletonBoneArr3[i2].prepareSkeleton(sLSkeletonBoneArr, i3)
                i2++
                i3 = prepareSkeleton2
            }
        }
        return i3
    }

    /* access modifiers changed from: package-private */
    fun setPositionOverride(lLVector3: LLVector3) {
        this.basePosition.set(lLVector3)
    }

    /* access modifiers changed from: package-private */
    val Unit updateGlobalPos(AnimationSkeletonData animationSkeletonData, FloatArray fArr, FloatArray fArr2) {
        Float f
        Float f2
        Float f3
        val i: Int = this.boneID.animatedIndex
        val i2: Int = i * 4
        val i3: Int = i * 16
        if (i >= 0) {
            val animOffsets: FloatArray = animationSkeletonData.getAnimOffsets()
            val f4: Float = animOffsets[i2 + 3]
            if (f4 > 0.0f) {
                val f5: Float = f4 * animOffsets[i2]
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
