package com.linkpoint.render.avatar

import android.opengl.Matrix
import com.linkpoint.slproto.avatar.MeshIndex
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.avatar.SLAvatarParams
import com.linkpoint.slproto.avatar.SLAvatarParams.AvatarParam
import com.linkpoint.slproto.avatar.SLAvatarParams.DrivenParam
import com.linkpoint.slproto.avatar.SLAvatarParams.ParamSet
import com.linkpoint.slproto.avatar.SLAvatarParams.SkeletonParamDefinition
import com.linkpoint.slproto.avatar.SLAvatarParams.SkeletonParamValue
import com.linkpoint.slproto.avatar.SLBaseAvatar
import com.linkpoint.slproto.avatar.SLDefaultSkeleton
import com.linkpoint.slproto.avatar.SLSkeletonBone
import com.linkpoint.slproto.avatar.SLSkeletonBoneID
import com.linkpoint.slproto.avatar.SLVisualParamID
import com.linkpoint.slproto.mesh.MeshJointTranslations
import com.linkpoint.slproto.types.LLVector3
import java.util.Arrays
import java.util.EnumMap
import java.util.Map
import java.util.Map.Entry
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.Nonnull

class AvatarSkeleton : SLDefaultSkeleton() {
    private val Array<SLSkeletonBone> animatedBones = SLSkeletonBone[133]
    private val Array<AttachmentPoint> attachmentPoints = AttachmentPoint[56]
    private val Float bodySize
    private val AtomicBoolean forceAnimate = AtomicBoolean(true)
    private val Boolean hasExtendedBones
    private val Map<MeshIndex, FloatArray> partMorphParams = EnumMap(MeshIndex.class)
    private val Float pelvisOffset
    private val Float pelvisToFoot

    @JvmStatic
private class AttachmentPoint {
        final SLSkeletonBone bone
        val FloatArray matrix
        val SLAttachmentPoint point

        private AttachmentPoint(SLSkeletonBone sLSkeletonBone, SLAttachmentPoint sLAttachmentPoint) {
            this.matrix = Float[16]
            this.bone = sLSkeletonBone
            this.point = sLAttachmentPoint
        }

        /* synthetic */ AttachmentPoint(SLSkeletonBone sLSkeletonBone, SLAttachmentPoint sLAttachmentPoint, AttachmentPoint attachmentPoint) {
            this(sLSkeletonBone, sLAttachmentPoint)
        }
    }

    AvatarSkeleton(AvatarShapeParams avatarShapeParams, MeshJointTranslations meshJointTranslations, Boolean z) {
        this.hasExtendedBones = z
        prepareSkeleton()
        for (Entry entry : this.bones.entrySet()) {
            i = ((SLSkeletonBoneID) entry.getKey()).animatedIndex
            if (i >= 0 && i < 133) {
                this.animatedBones[i] = (SLSkeletonBone) entry.getValue()
            }
        }
        val enumMap: Map = EnumMap(SLSkeletonBoneID.class)
        val instance: SLBaseAvatar = SLBaseAvatar.getInstance()
        applyJointTranslations(meshJointTranslations)
        this.pelvisOffset = meshJointTranslations.pelvisOffset
        for (MeshIndex meshIndex : MeshIndex.VALUES) {
            obj = Float[instance.getMeshEntry(meshIndex).polyMesh.getNumMorphs()]
            Arrays.fill(obj, 0.0f)
            this.partMorphParams.put(meshIndex, obj)
        }
        for (Object obj2 : SLSkeletonBoneID.VALUES) {
            enumMap.put(obj2, SkeletonParamValue(LLVector3(), LLVector3()))
            ((SkeletonParamValue) enumMap.get(obj2)).scale.set(1.0f, 1.0f, 1.0f)
            ((SkeletonParamValue) enumMap.get(obj2)).offset.set(0.0f, 0.0f, 0.0f)
        }
        val paramCount: Int = avatarShapeParams.getParamCount()
        for (Int i2 = 0; i2 < paramCount; i2++) {
            val paramSet: ParamSet = SLAvatarParams.paramDefs[i2]
            for (AvatarParam avatarParam : paramSet.params) {
                val paramValue: Float = ((((Float) avatarShapeParams.getParamValue(i2)) * (avatarParam.maxValue - avatarParam.minValue)) / 255.0f) + avatarParam.minValue
                ApplyMorphParam(instance, enumMap, avatarParam, paramSet.name, paramValue)
                if (avatarParam.drivenParams != null) {
                    for (DrivenParam drivenParam : avatarParam.drivenParams) {
                        val paramSet2: ParamSet = (ParamSet) SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenParam.drivenID))
                        if (paramSet2 != null) {
                            for (AvatarParam avatarParam2 : paramSet2.params) {
                                ApplyMorphParam(instance, enumMap, avatarParam2, paramSet2.name, getDrivenWeight(paramValue, avatarParam, drivenParam, avatarParam2))
                            }
                        }
                    }
                }
            }
        }
        for (Object obj22 : SLSkeletonBoneID.VALUES) {
            ((SLSkeletonBone) this.bones.get(obj22)).deformHierarchy(((SkeletonParamValue) enumMap.get(obj22)).offset, ((SkeletonParamValue) enumMap.get(obj22)).scale)
        }
        this.pelvisToFoot = super.getPelvisToFoot()
        this.bodySize = super.getBodySize()
        val i3: Int = 0
        while (true) {
            i = i3
            if (i < 56) {
                val sLAttachmentPoint: SLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i]
                if (!(sLAttachmentPoint == null || sLAttachmentPoint.isHUD)) {
                    val sLSkeletonBoneID: SLSkeletonBoneID = sLAttachmentPoint.bone
                    if (sLSkeletonBoneID != null) {
                        val sLSkeletonBone: SLSkeletonBone = (SLSkeletonBone) this.bones.get(sLSkeletonBoneID)
                        if (sLSkeletonBone != null) {
                            this.attachmentPoints[i] = AttachmentPoint(sLSkeletonBone, sLAttachmentPoint, null)
                        }
                    } else {
                        this.attachmentPoints[i] = AttachmentPoint(null, sLAttachmentPoint, null)
                    }
                }
                i3 = i + 1
            } else {
                updateAttachmentMatrix()
                return
            }
        }
    }

    private fun ApplyMorphParam(sLBaseAvatar: SLBaseAvatar, map: Map<SLSkeletonBoneID, SkeletonParamValue>, avatarParam: AvatarParam, sLVisualParamID: SLVisualParamID, f: Float) {
        if (avatarParam.morph && avatarParam.meshIndex != null) {
            val fArr: FloatArray = (FloatArray) this.partMorphParams.get(avatarParam.meshIndex)
            if (fArr != null) {
                val morphIndex: Int = sLBaseAvatar.getMeshEntry(avatarParam.meshIndex).polyMesh.getMorphIndex(sLVisualParamID)
                if (morphIndex != -1) {
                    fArr[morphIndex] = fArr[morphIndex] + f
                }
            }
        }
        if (avatarParam.skeletonParams != null) {
            for (Entry entry : avatarParam.skeletonParams.entrySet()) {
                val skeletonParamValue: SkeletonParamValue = (SkeletonParamValue) map.get(entry.getKey())
                val skeletonParamDefinition: SkeletonParamDefinition = (SkeletonParamDefinition) entry.getValue()
                if (skeletonParamDefinition.scale != null) {
                    skeletonParamValue.scale.mulWeighted(skeletonParamDefinition.scale, f)
                }
                if (skeletonParamDefinition.offset != null) {
                    skeletonParamValue.offset.addMul(skeletonParamDefinition.offset, f)
                }
            }
        }
    }

    @JvmStatic
     fun getDrivenWeight(f: Float, avatarParam: AvatarParam, drivenParam: DrivenParam, avatarParam2: AvatarParam): Float {
        val f2: Float = avatarParam.minValue
        val f3: Float = avatarParam.maxValue
        val f4: Float = avatarParam2.minValue
        val f5: Float = avatarParam2.maxValue
        if (f <= drivenParam.min1) {
            return (drivenParam.min1 != drivenParam.max1 || drivenParam.min1 > f2) ? f4 : f5
        } else {
            if (f <= drivenParam.max1) {
                return ((f5 - f4) * ((f - drivenParam.min1) / (drivenParam.max1 - drivenParam.min1))) + f4
            } else if (f <= drivenParam.max2) {
                return f5
            } else {
                if (f > drivenParam.min2) {
                    return drivenParam.max2 < f3 ? f4 : f5
                } else {
                    return f5 + ((f4 - f5) * ((f - drivenParam.max2) / (drivenParam.min2 - drivenParam.max2)))
                }
            }
        }
    }

     private fun updateAttachmentMatrix() {
        val fArr: FloatArray = Float[16]
        for (Int i = 0; i < 56; i++) {
            val attachmentPoint: AttachmentPoint = this.attachmentPoints[i]
            if (attachmentPoint != null) {
                val sLSkeletonBone: SLSkeletonBone = attachmentPoint.bone
                if (sLSkeletonBone != null) {
                    Matrix.translateM(fArr, 0, sLSkeletonBone.getGlobalMatrix(), 0, attachmentPoint.point.position.x * sLSkeletonBone.getScaleX(), attachmentPoint.point.position.y * sLSkeletonBone.getScaleY(), attachmentPoint.point.position.z * sLSkeletonBone.getScaleZ())
                    Matrix.multiplyMM(attachmentPoint.matrix, 0, fArr, 0, attachmentPoint.point.rotation.getInverseMatrix(), 0)
                } else {
                    Matrix.setIdentityM(fArr, 0)
                    Matrix.translateM(fArr, 0, this.rootBone.getPositionX(), this.rootBone.getPositionY(), this.rootBone.getPositionZ())
                    Matrix.translateM(fArr, 0, attachmentPoint.point.position.x, attachmentPoint.point.position.y, attachmentPoint.point.position.z)
                    Matrix.multiplyMM(attachmentPoint.matrix, 0, fArr, 0, attachmentPoint.point.rotation.getInverseMatrix(), 0)
                }
                val i2: Int = SLAttachmentPoint.attachmentPoints[i].nonHUDindex
                if (i2 >= 0) {
                    System.arraycopy(attachmentPoint.matrix, 0, this.jointWorldMatrix, (i2 + SLSkeletonBoneID.VALUES.length) * 16, 16)
                }
            }
        }
    }

    fun UpdateGlobalPositions(animationSkeletonData: AnimationSkeletonData) {
        super.UpdateGlobalPositions(animationSkeletonData)
        updateAttachmentMatrix()
    }

     public fun getAnimatedBone(i: Int): SLSkeletonBone {
        return this.animatedBones[i]
    }

    final FloatArray getAttachmentMatrix(Int i) {
        if (i >= 0 && i < this.attachmentPoints.length) {
            val attachmentPoint: AttachmentPoint = this.attachmentPoints[i]
            if (attachmentPoint != null) {
                return attachmentPoint.matrix
            }
        }
        return null
    }

    val Float getBodySize() {
        return this.bodySize
    }

    final FloatArray getMorphParams(MeshIndex meshIndex) {
        return (FloatArray) this.partMorphParams.get(meshIndex)
    }

    final Float getPelvisOffset() {
        return this.pelvisOffset
    }

    val Float getPelvisToFoot() {
        return this.pelvisToFoot
    }

     public fun hasExtendedBones(): Boolean {
        return this.hasExtendedBones
    }

     public fun needForceAnimate(): Boolean {
        return this.forceAnimate.getAndSet(false)
    }

    fun setForceAnimate() {
        this.forceAnimate.set(true)
    }
}
