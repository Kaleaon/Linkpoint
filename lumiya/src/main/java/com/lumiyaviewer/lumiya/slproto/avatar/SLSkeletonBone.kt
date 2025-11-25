package com.lumiyaviewer.lumiya.slproto.avatar

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.render.avatar.AnimationSkeletonData
import com.lumiyaviewer.lumiya.slproto.types.LLVector3

class SLSkeletonBone(
    val boneID: SLSkeletonBoneID,
    pos: LLVector3,
    rot: LLVector3,
    val childBones: Array<SLSkeletonBone>?,
    val collisionVolumes: Array<SLSkeletonBone>?
) {
    private val basePosition: LLVector3 = LLVector3(rot)
    private val defaultBasePosition: LLVector3 = LLVector3(basePosition)
    private val offset: LLVector3 = LLVector3()
    private val scale: LLVector3 = LLVector3(1.0f, 1.0f, 1.0f)
    private val usePosition: LLVector3 = if (boneID.isJoint) basePosition else LLVector3(pos)
    private var parent: SLSkeletonBone? = null
    private var globalBaseX: Float = 0.0f
    private var globalBaseY: Float = 0.0f
    private var globalBaseZ: Float = 0.0f
    private val globalMatrix: FloatArray = FloatArray(16)
    private val tempMatrix: FloatArray = FloatArray(16)
    private val boneIndex: Int = boneID.ordinal

    init {
        childBones?.forEach { it.parent = this }
        collisionVolumes?.forEach { it.parent = this }
    }

    fun deform(v1: LLVector3, v2: LLVector3) {
        offset.add(v1)
        scale.mul(v2)
    }

    fun deformHierarchy(v1: LLVector3, v2: LLVector3) {
        offset.add(v1)
        scale.mul(v2)
        collisionVolumes?.forEach { it.deform(v1, v2) }
    }

    fun getBasePosition(): LLVector3 = basePosition

    fun getGlobalMatrix(): FloatArray = globalMatrix

    fun getPositionX(): Float = basePosition.x + offset.x
    fun getPositionY(): Float = basePosition.y + offset.y
    fun getPositionZ(): Float = basePosition.z + offset.z

    fun getScaleX(): Float = scale.x
    fun getScaleY(): Float = scale.y
    fun getScaleZ(): Float = scale.z

    fun prepareSkeleton(arr: Array<SLSkeletonBone?>, i: Int): Int {
        var currentIndex = i
        arr[currentIndex++] = this
        
        if (parent == null) {
            globalBaseX = defaultBasePosition.x
            globalBaseY = defaultBasePosition.y
            globalBaseZ = defaultBasePosition.z
        } else {
            globalBaseX = parent!!.globalBaseX + defaultBasePosition.x
            globalBaseY = parent!!.globalBaseY + defaultBasePosition.y
            globalBaseZ = parent!!.globalBaseZ + defaultBasePosition.z
        }

        childBones?.forEach { currentIndex = it.prepareSkeleton(arr, currentIndex) }
        collisionVolumes?.forEach { currentIndex = it.prepareSkeleton(arr, currentIndex) }
        
        return currentIndex
    }

    fun setPositionOverride(v: LLVector3) {
        basePosition.set(v)
    }

    fun updateGlobalPos(data: AnimationSkeletonData, fArr: FloatArray, fArr2: FloatArray) {
        var f = 0.0f
        var f2 = 0.0f
        var f3 = 0.0f
        val i = boneID.animatedIndex
        val i2 = i * 4
        val i3 = i * 16

        if (i >= 0) {
            val animOffsets = data.getAnimOffsets()
            val f4 = animOffsets[i2 + 3]
            if (f4 > 0.0f) {
                f3 = f4 * animOffsets[i2]
                f2 = f4 * animOffsets[i2 + 1]
                f = animOffsets[i2 + 2] * f4
            }
        }

        val parentBone = parent
        if (parentBone != null) {
            Matrix.translateM(
                tempMatrix, 0, parentBone.globalMatrix, 0,
                f3 + (usePosition.x * parentBone.scale.x) + offset.x,
                f2 + (usePosition.y * parentBone.scale.y) + offset.y,
                (usePosition.z * parentBone.scale.z) + offset.z + f
            )
        } else {
            Matrix.setIdentityM(tempMatrix, 0)
            Matrix.translateM(
                tempMatrix, 0,
                usePosition.x + offset.x + f3,
                usePosition.y + offset.y + f2,
                f + usePosition.z + offset.z
            )
        }

        if (i >= 0) {
            Matrix.multiplyMM(globalMatrix, 0, tempMatrix, 0, data.getAnimMatrix(), i3)
        } else {
            System.arraycopy(tempMatrix, 0, globalMatrix, 0, 16)
        }

        Matrix.scaleM(fArr2, boneIndex * 16, globalMatrix, 0, scale.x, scale.y, scale.z)
        Matrix.translateM(fArr, boneIndex * 16, fArr2, boneIndex * 16, -globalBaseX, -globalBaseY, -globalBaseZ)
    }
}
