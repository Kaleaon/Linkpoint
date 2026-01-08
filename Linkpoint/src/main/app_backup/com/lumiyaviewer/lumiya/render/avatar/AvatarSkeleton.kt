package com.lumiyaviewer.lumiya.render.avatar

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.slproto.avatar.*
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.*
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
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
        val avatar = SLBaseAvatar.instance
        
        applyJointTranslations(meshJointTranslations)
        pelvisOffset = meshJointTranslations.pelvisOffset
        
        // Initialize morph parameters for each mesh
        for (meshIndex in MeshIndex.values()) {
            val meshEntry = avatar?.getMeshEntry(meshIndex)
            if (meshEntry != null) {
                 // Assuming meshEntry.polyMesh is not null if meshEntry exists
                 // Assuming polyMesh has getNumMorphs()
                 // If SLBaseAvatar or its methods are different, we'd need to fix that.
                 // Based on decompiled code, polyMesh seems to be a field.
                val numMorphs = meshEntry.polyMesh?.getNumMorphs() ?: 0
                val morphArray = FloatArray(numMorphs)
                Arrays.fill(morphArray, 0.0f)
                partMorphParams[meshIndex] = morphArray
            }
        }
        
        // Initialize skeleton param values
        for (boneId in SLSkeletonBoneID.values()) {
            val paramValue = SkeletonParamValue(LLVector3(), LLVector3())
            paramValue.scale.set(1.0f, 1.0f, 1.0f)
            paramValue.offset.set(0.0f, 0.0f, 0.0f)
            skeletonParams[boneId] = paramValue
        }
        
        // Apply visual parameters from shape
        val paramCount = avatarShapeParams.getParamCount()
        // paramDefs is an array in SLAvatarParams. Assuming static access via Companion or singleton instance if converted to object.
        // The decompiled SLAvatarParams.kt (see next file content) is a class with instance block initialization,
        // which is problematic for static access unless it's an object.
        // We need to access SLAvatarParams.paramDefs.
        
        // Assuming SLAvatarParams is an object or has a static instance 'instance'
        val paramsInstance = SLAvatarParams.getInstance()
        
        for (i in 0 until paramCount) {
            val paramSet = paramsInstance.paramDefs[i] ?: continue
            
            for (avatarParam in paramSet.params) {
                val rawValue = avatarShapeParams.getParamValue(i).toFloat()
                val paramValue = ((rawValue * (avatarParam.maxValue - avatarParam.minValue)) / 255.0f) + avatarParam.minValue
                
                if (avatar != null) {
                    ApplyMorphParam(avatar, skeletonParams, avatarParam, paramSet.name, paramValue)
                }
                
                // Apply driven parameters
                avatarParam.drivenParams?.forEach { drivenParam ->
                    val drivenParamSet = paramsInstance.paramByIDs[drivenParam.drivenID]
                    drivenParamSet?.params?.forEach { drivenAvatarParam ->
                        val drivenWeight = getDrivenWeight(paramValue, avatarParam, drivenParam, drivenAvatarParam)
                        if (avatar != null) {
                            ApplyMorphParam(avatar, skeletonParams, drivenAvatarParam, drivenParamSet.name, drivenWeight)
                        }
                    }
                }
            }
        }
        
        // Apply deformations to skeleton
        for (boneId in SLSkeletonBoneID.values()) {
            val bone = bones[boneId]
            val params = skeletonParams[boneId]
            if (params != null) {
                bone?.deformHierarchy(params.offset, params.scale)
            }
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
        // Note: avatarParam.morph is Boolean?, so use safe call or check true
        if (avatarParam.morph == true && avatarParam.meshIndex != null) {
            partMorphParams[avatarParam.meshIndex]?.let { morphArray ->
                val meshEntry = avatar.getMeshEntry(avatarParam.meshIndex!!)
                // meshEntry.polyMesh might be null if not loaded?
                val morphIndex = meshEntry?.polyMesh?.getMorphIndex(paramId) ?: -1
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
        val drivingMin = drivenParam.min1 // Using min1/max1 from DrivenParam, assuming logic matches intention
        // The decompiled logic used drivenParam fields for ranges, but drivingParam for bounds check?
        // Let's stick to decompiled logic structure but using correct fields.
        
        // Re-reading decompiled:
        // drivingMin/Max were from drivingParam.
        // drivenMin/Max were from drivenAvatarParam.
        // Ranges check drivingValue against drivenParam.min1/max1/min2/max2.
        
        val drivingMinVal = drivingParam.minValue
        // val drivingMaxVal = drivingParam.maxValue // Unused in decompiled logic explicitly?
        
        val drivenMin = drivenAvatarParam.minValue
        val drivenMax = drivenAvatarParam.maxValue
        
        return when {
            drivingValue <= drivenParam.min1 -> {
                if (drivenParam.min1 != drivenParam.max1 || drivenParam.min1 > drivingMinVal) {
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
                // Note: decompiled had drivingMaxVal check here
                if (drivenParam.max2 < drivingParam.maxValue) drivenMin else drivenMax
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
            val nonHUDIndex = SLAttachmentPoint.attachmentPoints[i]?.nonHUDindex ?: -1
            if (nonHUDIndex >= 0) {
                System.arraycopy(
                    attachment.matrix, 0,
                    jointWorldMatrix,
                    (nonHUDIndex + SLSkeletonBoneID.values().size) * 16,
                    16
                )
            }
        }
    }

    /**
     * Update global positions with animation data
     */
    override fun UpdateGlobalPositions(animationSkeletonData: AnimationSkeletonData) {
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
    override fun getBodySize(): Float = bodySize

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
    override fun getPelvisToFoot(): Float = pelvisToFoot

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
