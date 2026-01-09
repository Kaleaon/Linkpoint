package com.linkpoint.render.avatar

import android.opengl.Matrix
import com.linkpoint.slproto.avatar.*
import com.linkpoint.slproto.avatar.SLAvatarParams.*
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.types.LLVector3
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Avatar skeleton with shape morphing and attachment points
 * Manages bone hierarchy and visual parameters
 */
class AvatarSkeleton(
    avatarShapeParams: AvatarShapeParams,
    meshJointTranslations: MeshJointTranslations,
    hasExtendedBones: Boolean
) : SLDefaultSkeleton() {
    
    private val animatedBones = Array<SLSkeletonBone?>(133) { null }
    private val attachmentPoints = Array<AttachmentPoint?>(56) { null }
    private val bodySize: Float
    private val forceAnimate = AtomicBoolean(true)
    private val hasExtendedBones: Boolean
    private val partMorphParams = EnumMap<MeshIndex, FloatArray>(MeshIndex::class.java)
    private val pelvisOffset: Float
    private val pelvisToFoot: Float

    /**
     * Attachment point for worn objects
     */
    private class AttachmentPoint(
        val bone: SLSkeletonBone?,
        val point: SLAttachmentPoint
    ) {
        val matrix = FloatArray(16)
    }

    init {
        this.hasExtendedBones = hasExtendedBones
        prepareSkeleton()
        
        // Map bones to animated bone array
        for ((boneId, bone) in bones) {
            val index = boneId.animatedIndex
            if (index in 0..132) {
                animatedBones[index] = bone
            }
        }
        
        // Initialize skeleton param values
        val skeletonParams = EnumMap<SLSkeletonBoneID, SkeletonParamValue>(SLSkeletonBoneID::class.java)
        val avatar = SLBaseAvatar.getInstance()
        
        applyJointTranslations(meshJointTranslations)
        pelvisOffset = meshJointTranslations.pelvisOffset
        
        // Initialize morph parameters for each mesh
        for (meshIndex in MeshIndex.VALUES) {
            val numMorphs = avatar.getMeshEntry(meshIndex).polyMesh.getNumMorphs()
            val morphArray = FloatArray(numMorphs)
            Arrays.fill(morphArray, 0.0f)
            partMorphParams[meshIndex] = morphArray
        }
        
        // Initialize skeleton param values
        for (boneId in SLSkeletonBoneID.VALUES) {
            val paramValue = SkeletonParamValue(LLVector3(), LLVector3())
            paramValue.scale.set(1.0f, 1.0f, 1.0f)
            paramValue.offset.set(0.0f, 0.0f, 0.0f)
            skeletonParams[boneId] = paramValue
        }
        
        // Apply visual parameters from shape
        val paramCount = avatarShapeParams.getParamCount()
        for (i in 0 until paramCount) {
            val paramSet = SLAvatarParams.paramDefs[i]
            
            for (avatarParam in paramSet.params) {
                val rawValue = avatarShapeParams.getParamValue(i).toFloat()
                val paramValue = ((rawValue * (avatarParam.maxValue - avatarParam.minValue)) / 255.0f) + avatarParam.minValue
                
                ApplyMorphParam(avatar, skeletonParams, avatarParam, paramSet.name, paramValue)
                
                // Apply driven parameters
                avatarParam.drivenParams?.forEach { drivenParam ->
                    val drivenParamSet = SLAvatarParams.paramByIDs[drivenParam.drivenID]
                    drivenParamSet?.params?.forEach { drivenAvatarParam ->
                        val drivenWeight = getDrivenWeight(paramValue, avatarParam, drivenParam, drivenAvatarParam)
                        ApplyMorphParam(avatar, skeletonParams, drivenAvatarParam, drivenParamSet.name, drivenWeight)
                    }
                }
            }
        }
        
        // Apply deformations to skeleton
        for (boneId in SLSkeletonBoneID.VALUES) {
            val bone = bones[boneId]
            val params = skeletonParams[boneId]
            bone?.deformHierarchy(params?.offset, params?.scale)
        }
        
        pelvisToFoot = super.getPelvisToFoot()
        bodySize = super.getBodySize()
        
        // Initialize attachment points
        for (i in 0 until 56) {
            val attachPoint = SLAttachmentPoint.attachmentPoints[i]
            
            if (attachPoint != null && !attachPoint.isHUD) {
                val bone = attachPoint.bone?.let { bones[it] }
                attachmentPoints[i] = AttachmentPoint(bone, attachPoint)
            }
        }
        
        updateAttachmentMatrix()
    }

    /**
     * Apply morph parameter to avatar
     */
    private fun ApplyMorphParam(
        avatar: SLBaseAvatar,
        skeletonParams: MutableMap<SLSkeletonBoneID, SkeletonParamValue>,
        avatarParam: AvatarParam,
        paramId: SLVisualParamID,
        value: Float
    ) {
        // Apply mesh morphs
        if (avatarParam.morph && avatarParam.meshIndex != null) {
            partMorphParams[avatarParam.meshIndex]?.let { morphArray ->
                val morphIndex = avatar.getMeshEntry(avatarParam.meshIndex!!).polyMesh.getMorphIndex(paramId)
                if (morphIndex != -1) {
                    morphArray[morphIndex] += value
                }
            }
        }
        
        // Apply skeleton deformations
        avatarParam.skeletonParams?.forEach { (boneId, paramDef) ->
            skeletonParams[boneId]?.let { paramValue ->
                paramDef.scale?.let { scale ->
                    paramValue.scale.mulWeighted(scale, value)
                }
                paramDef.offset?.let { offset ->
                    paramValue.offset.addMul(offset, value)
                }
            }
        }
    }

    /**
     * Calculate driven parameter weight
     */
    fun getDrivenWeight(
        drivingValue: Float,
        drivingParam: AvatarParam,
        drivenParam: DrivenParam,
        drivenAvatarParam: AvatarParam
    ): Float {
        val drivingMin = drivingParam.minValue
        val drivingMax = drivingParam.maxValue
        val drivenMin = drivenAvatarParam.minValue
        val drivenMax = drivenAvatarParam.maxValue
        
        return when {
            drivingValue <= drivenParam.min1 -> {
                if (drivenParam.min1 != drivenParam.max1 || drivenParam.min1 > drivingMin) {
                    drivenMin
                } else {
                    drivenMax
                }
            }
            drivingValue <= drivenParam.max1 -> {
                val t = (drivingValue - drivenParam.min1) / (drivenParam.max1 - drivenParam.min1)
                ((drivenMax - drivenMin) * t) + drivenMin
            }
            drivingValue <= drivenParam.max2 -> {
                drivenMax
            }
            drivingValue > drivenParam.min2 -> {
                if (drivenParam.max2 < drivingMax) drivenMin else drivenMax
            }
            else -> {
                val t = (drivingValue - drivenParam.max2) / (drivenParam.min2 - drivenParam.max2)
                drivenMax + ((drivenMin - drivenMax) * t)
            }
        }
    }

    /**
     * Update attachment matrices
     */
    private fun updateAttachmentMatrix() {
        val tempMatrix = FloatArray(16)
        
        for (i in 0 until 56) {
            val attachment = attachmentPoints[i] ?: continue
            
            val bone = attachment.bone
            if (bone != null) {
                // Attachment to bone
                Matrix.translateM(
                    tempMatrix, 0,
                    bone.getGlobalMatrix(), 0,
                    attachment.point.position.x * bone.getScaleX(),
                    attachment.point.position.y * bone.getScaleY(),
                    attachment.point.position.z * bone.getScaleZ()
                )
                Matrix.multiplyMM(
                    attachment.matrix, 0,
                    tempMatrix, 0,
                    attachment.point.rotation.getInverseMatrix(), 0
                )
            } else {
                // Attachment to root
                Matrix.setIdentityM(tempMatrix, 0)
                Matrix.translateM(
                    tempMatrix, 0,
                    rootBone.getPositionX(),
                    rootBone.getPositionY(),
                    rootBone.getPositionZ()
                )
                Matrix.translateM(
                    tempMatrix, 0,
                    attachment.point.position.x,
                    attachment.point.position.y,
                    attachment.point.position.z
                )
                Matrix.multiplyMM(
                    attachment.matrix, 0,
                    tempMatrix, 0,
                    attachment.point.rotation.getInverseMatrix(), 0
                )
            }
            
            // Update joint world matrix for rendering
            val nonHUDIndex = SLAttachmentPoint.attachmentPoints[i].nonHUDindex
            if (nonHUDIndex >= 0) {
                System.arraycopy(
                    attachment.matrix, 0,
                    jointWorldMatrix,
                    (nonHUDIndex + SLSkeletonBoneID.VALUES.size) * 16,
                    16
                )
            }
        }
    }

    /**
     * Update global positions with animation data
     */
    fun UpdateGlobalPositions(animationSkeletonData: AnimationSkeletonData) {
        super.UpdateGlobalPositions(animationSkeletonData)
        updateAttachmentMatrix()
    }

    /**
     * Get animated bone by index
     */
    fun getAnimatedBone(index: Int): SLSkeletonBone? {
        return animatedBones.getOrNull(index)
    }

    /**
     * Get attachment matrix
     */
    fun getAttachmentMatrix(index: Int): FloatArray? {
        return if (index in attachmentPoints.indices) {
            attachmentPoints[index]?.matrix
        } else {
            null
        }
    }

    /**
     * Get body size
     */
    fun getBodySize(): Float = bodySize

    /**
     * Get morph parameters for mesh
     */
    fun getMorphParams(meshIndex: MeshIndex): FloatArray? {
        return partMorphParams[meshIndex]
    }

    /**
     * Get pelvis offset
     */
    fun getPelvisOffset(): Float = pelvisOffset

    /**
     * Get pelvis to foot distance
     */
    fun getPelvisToFoot(): Float = pelvisToFoot

    /**
     * Check if skeleton has extended bones
     */
    fun hasExtendedBones(): Boolean = hasExtendedBones

    /**
     * Check if skeleton needs forced animation update
     */
    fun needForceAnimate(): Boolean {
        return forceAnimate.getAndSet(false)
    }

    /**
     * Set forced animation flag
     */
    fun setForceAnimate() {
        forceAnimate.set(true)
    }
}
