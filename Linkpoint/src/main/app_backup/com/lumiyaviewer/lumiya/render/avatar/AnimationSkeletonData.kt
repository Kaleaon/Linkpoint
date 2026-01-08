package com.lumiyaviewer.lumiya.render.avatar

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.Arrays

class AnimationSkeletonData {
    private val numAnimatedBones = 133
    private var animMatrix: FloatArray = FloatArray(2128)
    private var animMatrix_Swap: FloatArray = FloatArray(2128)
    private var animOffsets: FloatArray = FloatArray(532)
    private var animOffsets_Swap: FloatArray = FloatArray(532)
    
    private val animPosArray = Array(numAnimatedBones) { LLVector3() }
    private val animPriorityPosArray = FloatArray(numAnimatedBones)
    private val animPriorityRotArray = FloatArray(numAnimatedBones)
    private val animRotArray = Array(numAnimatedBones) { LLQuaternion() }

    init {
        for (i in 0 until numAnimatedBones) {
            Matrix.setIdentityM(animMatrix, i * 16)
        }
        Arrays.fill(animOffsets, 0.0f)
    }

    fun animate(avatarSkeleton: AvatarSkeleton, avatarAnimationList: AvatarAnimationList) {
        Arrays.fill(animPriorityRotArray, 1.0f)
        Arrays.fill(animPriorityPosArray, 1.0f)
        for (i in 0 until numAnimatedBones) {
            animRotArray[i].setZero()
            animPosArray[i].set(0.0f, 0.0f, 0.0f)
        }
        
        avatarAnimationList.animate(avatarSkeleton, animPriorityRotArray, animPriorityPosArray, animRotArray, animPosArray)
        
        for (i in 0 until numAnimatedBones) {
            // Assuming getInverseMatrix writes to the array at offset
            animRotArray[i].getInverseMatrix(animMatrix_Swap, i * 16)
            animOffsets_Swap[(i * 4) + 0] = animPosArray[i].x
            animOffsets_Swap[(i * 4) + 1] = animPosArray[i].y
            animOffsets_Swap[(i * 4) + 2] = animPosArray[i].z
            animOffsets_Swap[(i * 4) + 3] = 1.0f - animPriorityPosArray[i]
        }
        
        // Swap buffers
        val tempMatrix = animMatrix
        animMatrix = animMatrix_Swap
        animMatrix_Swap = tempMatrix
        
        val tempOffsets = animOffsets
        animOffsets = animOffsets_Swap
        animOffsets_Swap = tempOffsets
    }

    fun getAnimMatrix(): FloatArray {
        return animMatrix
    }

    fun getAnimOffsets(): FloatArray {
        return animOffsets
    }
}
