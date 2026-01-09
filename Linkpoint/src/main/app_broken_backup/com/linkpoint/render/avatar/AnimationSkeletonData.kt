package com.linkpoint.render.avatar

import android.opengl.Matrix
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.util.Arrays

class AnimationSkeletonData {
    private Int numAnimatedBones = 133
    private val animMatrix: FloatArray = FloatArray(2128)
    private val animMatrix_Swap: FloatArray = FloatArray(2128)
    private val animOffsets: FloatArray = FloatArray(532)
    private val animOffsets_Swap: FloatArray = FloatArray(532)
    private LLVector3[] animPosArray = LLVector3[133]
    private val animPriorityPosArray: FloatArray = FloatArray(133)
    private val animPriorityRotArray: FloatArray = FloatArray(133)
    private LLQuaternion[] animRotArray = LLQuaternion[133]

    AnimationSkeletonData() {
        for (i in 0 until 133) {
            Matrix.setIdentityM(this.animMatrix, i * 16)
            this.animPosArray[i] = LLVector3()
            this.animRotArray[i] = LLQuaternion()
        }
        Arrays.fill(this.animOffsets, 0.0f)
    }

    fun animate(AvatarSkeleton avatarSkeleton, AvatarAnimationList avatarAnimationList)  {
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
        FloatArray fArr = this.animMatrix
        this.animMatrix = this.animMatrix_Swap
        this.animMatrix_Swap = fArr
        fArr = this.animOffsets
        this.animOffsets = this.animOffsets_Swap
        this.animOffsets_Swap = fArr
    }

    fun getAnimMatrix(): FloatArray {
        return this.animMatrix
    }

    fun getAnimOffsets(): FloatArray {
        return this.animOffsets
    }
}
