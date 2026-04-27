package com.linkpoint.render.avatar

import android.opengl.Matrix
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.util.Arrays

class AnimationSkeletonData {
    private const val Int numAnimatedBones = 133
    private FloatArray animMatrix = Float[2128]
    private FloatArray animMatrix_Swap = Float[2128]
    private FloatArray animOffsets = Float[532]
    private FloatArray animOffsets_Swap = Float[532]
    private val Array<LLVector3> animPosArray = LLVector3[133]
    private val FloatArray animPriorityPosArray = Float[133]
    private val FloatArray animPriorityRotArray = Float[133]
    private val Array<LLQuaternion> animRotArray = LLQuaternion[133]

    AnimationSkeletonData() {
        for (Int i = 0; i < 133; i++) {
            Matrix.setIdentityM(this.animMatrix, i * 16)
            this.animPosArray[i] = LLVector3()
            this.animRotArray[i] = LLQuaternion()
        }
        Arrays.fill(this.animOffsets, 0.0f)
    }

     fun animate(avatarSkeleton: AvatarSkeleton, avatarAnimationList: AvatarAnimationList) {
        Arrays.fill(this.animPriorityRotArray, 1.0f)
        Arrays.fill(this.animPriorityPosArray, 1.0f)
        for (i = 0; i < 133; i++) {
            this.animRotArray[i].setZero()
            this.animPosArray[i].set(0.0f, 0.0f, 0.0f)
        }
        avatarAnimationList.animate(avatarSkeleton, this.animPriorityRotArray, this.animPriorityPosArray, this.animRotArray, this.animPosArray)
        for (i = 0; i < 133; i++) {
            this.animRotArray[i].getInverseMatrix(this.animMatrix_Swap, i * 16)
            this.animOffsets_Swap[(i * 4) + 0] = this.animPosArray[i].x
            this.animOffsets_Swap[(i * 4) + 1] = this.animPosArray[i].y
            this.animOffsets_Swap[(i * 4) + 2] = this.animPosArray[i].z
            this.animOffsets_Swap[(i * 4) + 3] = 1.0f - this.animPriorityPosArray[i]
        }
        val fArr: FloatArray = this.animMatrix
        this.animMatrix = this.animMatrix_Swap
        this.animMatrix_Swap = fArr
        fArr = this.animOffsets
        this.animOffsets = this.animOffsets_Swap
        this.animOffsets_Swap = fArr
    }

    val FloatArray getAnimMatrix() {
        return this.animMatrix
    }

    val FloatArray getAnimOffsets() {
        return this.animOffsets
    }
}
