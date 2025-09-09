package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.DrivenParam;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamDefinition;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.SkeletonParamValue;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import com.lumiyaviewer.lumiya.slproto.avatar.SLDefaultSkeleton;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.avatar.SLVisualParamID;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;

public class AvatarSkeleton extends SLDefaultSkeleton {
    private final SLSkeletonBone[] animatedBones = new SLSkeletonBone[133];
    private final AttachmentPoint[] attachmentPoints = new AttachmentPoint[56];
    private final float bodySize;
    private final AtomicBoolean forceAnimate = new AtomicBoolean(true);
    private final boolean hasExtendedBones;
    private final Map<MeshIndex, float[]> partMorphParams = new EnumMap(MeshIndex.class);
    private final float pelvisOffset;
    private final float pelvisToFoot;

    private static class AttachmentPoint {
        final SLSkeletonBone bone;
        public final float[] matrix;
        public final SLAttachmentPoint point;

        private AttachmentPoint(SLSkeletonBone sLSkeletonBone, SLAttachmentPoint sLAttachmentPoint) {
            this.matrix = new float[16];
            this.bone = sLSkeletonBone;
            this.point = sLAttachmentPoint;
        }

        /* synthetic */ AttachmentPoint(SLSkeletonBone sLSkeletonBone, SLAttachmentPoint sLAttachmentPoint, AttachmentPoint attachmentPoint) {
            this(sLSkeletonBone, sLAttachmentPoint);
        }
    }

    AvatarSkeleton(@Nonnull AvatarShapeParams avatarShapeParams, @Nonnull MeshJointTranslations meshJointTranslations, boolean z) {
        int i;
        Object obj;
        this.hasExtendedBones = z;
        prepareSkeleton();
        for (Entry entry : this.bones.entrySet()) {
            i = ((SLSkeletonBoneID) entry.getKey()).animatedIndex;
            if (i >= 0 && i < 133) {
                this.animatedBones[i] = (SLSkeletonBone) entry.getValue();
            }
        }
        Map enumMap = new EnumMap(SLSkeletonBoneID.class);
        SLBaseAvatar instance = SLBaseAvatar.getInstance();
        applyJointTranslations(meshJointTranslations);
        this.pelvisOffset = meshJointTranslations.pelvisOffset;
        for (MeshIndex meshIndex : MeshIndex.VALUES) {
            obj = new float[instance.getMeshEntry(meshIndex).polyMesh.getNumMorphs()];
            Arrays.fill(obj, 0.0f);
            this.partMorphParams.put(meshIndex, obj);
        }
        for (Object obj2 : SLSkeletonBoneID.VALUES) {
            enumMap.put(obj2, new SkeletonParamValue(new LLVector3(), new LLVector3()));
            ((SkeletonParamValue) enumMap.get(obj2)).scale.set(1.0f, 1.0f, 1.0f);
            ((SkeletonParamValue) enumMap.get(obj2)).offset.set(0.0f, 0.0f, 0.0f);
        }
        int paramCount = avatarShapeParams.getParamCount();
        for (int i2 = 0; i2 < paramCount; i2++) {
            ParamSet paramSet = SLAvatarParams.paramDefs[i2];
            for (AvatarParam avatarParam : paramSet.params) {
                float paramValue = ((((float) avatarShapeParams.getParamValue(i2)) * (avatarParam.maxValue - avatarParam.minValue)) / 255.0f) + avatarParam.minValue;
                ApplyMorphParam(instance, enumMap, avatarParam, paramSet.name, paramValue);
                if (avatarParam.drivenParams != null) {
                    for (DrivenParam drivenParam : avatarParam.drivenParams) {
                        ParamSet paramSet2 = (ParamSet) SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenParam.drivenID));
                        if (paramSet2 != null) {
                            for (AvatarParam avatarParam2 : paramSet2.params) {
                                ApplyMorphParam(instance, enumMap, avatarParam2, paramSet2.name, getDrivenWeight(paramValue, avatarParam, drivenParam, avatarParam2));
                            }
                        }
                    }
                }
            }
        }
        for (Object obj22 : SLSkeletonBoneID.VALUES) {
            ((SLSkeletonBone) this.bones.get(obj22)).deformHierarchy(((SkeletonParamValue) enumMap.get(obj22)).offset, ((SkeletonParamValue) enumMap.get(obj22)).scale);
        }
        this.pelvisToFoot = super.getPelvisToFoot();
        this.bodySize = super.getBodySize();
        int i3 = 0;
        while (true) {
            i = i3;
            if (i < 56) {
                SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i];
                if (!(sLAttachmentPoint == null || sLAttachmentPoint.isHUD)) {
                    SLSkeletonBoneID sLSkeletonBoneID = sLAttachmentPoint.bone;
                    if (sLSkeletonBoneID != null) {
                        SLSkeletonBone sLSkeletonBone = (SLSkeletonBone) this.bones.get(sLSkeletonBoneID);
                        if (sLSkeletonBone != null) {
                            this.attachmentPoints[i] = new AttachmentPoint(sLSkeletonBone, sLAttachmentPoint, null);
                        }
                    } else {
                        this.attachmentPoints[i] = new AttachmentPoint(null, sLAttachmentPoint, null);
                    }
                }
                i3 = i + 1;
            } else {
                updateAttachmentMatrix();
                return;
            }
        }
    }

    private void ApplyMorphParam(SLBaseAvatar sLBaseAvatar, Map<SLSkeletonBoneID, SkeletonParamValue> map, AvatarParam avatarParam, SLVisualParamID sLVisualParamID, float f) {
        if (avatarParam.morph && avatarParam.meshIndex != null) {
            float[] fArr = (float[]) this.partMorphParams.get(avatarParam.meshIndex);
            if (fArr != null) {
                int morphIndex = sLBaseAvatar.getMeshEntry(avatarParam.meshIndex).polyMesh.getMorphIndex(sLVisualParamID);
                if (morphIndex != -1) {
                    fArr[morphIndex] = fArr[morphIndex] + f;
                }
            }
        }
        if (avatarParam.skeletonParams != null) {
            for (Entry entry : avatarParam.skeletonParams.entrySet()) {
                SkeletonParamValue skeletonParamValue = (SkeletonParamValue) map.get(entry.getKey());
                SkeletonParamDefinition skeletonParamDefinition = (SkeletonParamDefinition) entry.getValue();
                if (skeletonParamDefinition.scale != null) {
                    skeletonParamValue.scale.mulWeighted(skeletonParamDefinition.scale, f);
                }
                if (skeletonParamDefinition.offset != null) {
                    skeletonParamValue.offset.addMul(skeletonParamDefinition.offset, f);
                }
            }
        }
    }

    public static float getDrivenWeight(float f, AvatarParam avatarParam, DrivenParam drivenParam, AvatarParam avatarParam2) {
        float f2 = avatarParam.minValue;
        float f3 = avatarParam.maxValue;
        float f4 = avatarParam2.minValue;
        float f5 = avatarParam2.maxValue;
        if (f <= drivenParam.min1) {
            return (drivenParam.min1 != drivenParam.max1 || drivenParam.min1 > f2) ? f4 : f5;
        } else {
            if (f <= drivenParam.max1) {
                return ((f5 - f4) * ((f - drivenParam.min1) / (drivenParam.max1 - drivenParam.min1))) + f4;
            } else if (f <= drivenParam.max2) {
                return f5;
            } else {
                if (f > drivenParam.min2) {
                    return drivenParam.max2 < f3 ? f4 : f5;
                } else {
                    return f5 + ((f4 - f5) * ((f - drivenParam.max2) / (drivenParam.min2 - drivenParam.max2)));
                }
            }
        }
    }

    private void updateAttachmentMatrix() {
        float[] fArr = new float[16];
        for (int i = 0; i < 56; i++) {
            AttachmentPoint attachmentPoint = this.attachmentPoints[i];
            if (attachmentPoint != null) {
                SLSkeletonBone sLSkeletonBone = attachmentPoint.bone;
                if (sLSkeletonBone != null) {
                    Matrix.translateM(fArr, 0, sLSkeletonBone.getGlobalMatrix(), 0, attachmentPoint.point.position.x * sLSkeletonBone.getScaleX(), attachmentPoint.point.position.y * sLSkeletonBone.getScaleY(), attachmentPoint.point.position.z * sLSkeletonBone.getScaleZ());
                    Matrix.multiplyMM(attachmentPoint.matrix, 0, fArr, 0, attachmentPoint.point.rotation.getInverseMatrix(), 0);
                } else {
                    Matrix.setIdentityM(fArr, 0);
                    Matrix.translateM(fArr, 0, this.rootBone.getPositionX(), this.rootBone.getPositionY(), this.rootBone.getPositionZ());
                    Matrix.translateM(fArr, 0, attachmentPoint.point.position.x, attachmentPoint.point.position.y, attachmentPoint.point.position.z);
                    Matrix.multiplyMM(attachmentPoint.matrix, 0, fArr, 0, attachmentPoint.point.rotation.getInverseMatrix(), 0);
                }
                int i2 = SLAttachmentPoint.attachmentPoints[i].nonHUDindex;
                if (i2 >= 0) {
                    System.arraycopy(attachmentPoint.matrix, 0, this.jointWorldMatrix, (i2 + SLSkeletonBoneID.VALUES.length) * 16, 16);
                }
            }
        }
    }

    public void UpdateGlobalPositions(AnimationSkeletonData animationSkeletonData) {
        super.UpdateGlobalPositions(animationSkeletonData);
        updateAttachmentMatrix();
    }

    public SLSkeletonBone getAnimatedBone(int i) {
        return this.animatedBones[i];
    }

    final float[] getAttachmentMatrix(int i) {
        if (i >= 0 && i < this.attachmentPoints.length) {
            AttachmentPoint attachmentPoint = this.attachmentPoints[i];
            if (attachmentPoint != null) {
                return attachmentPoint.matrix;
            }
        }
        return null;
    }

    public final float getBodySize() {
        return this.bodySize;
    }

    final float[] getMorphParams(MeshIndex meshIndex) {
        return (float[]) this.partMorphParams.get(meshIndex);
    }

    final float getPelvisOffset() {
        return this.pelvisOffset;
    }

    public final float getPelvisToFoot() {
        return this.pelvisToFoot;
    }

    public boolean hasExtendedBones() {
        return this.hasExtendedBones;
    }

    public boolean needForceAnimate() {
        return this.forceAnimate.getAndSet(false);
    }

    public void setForceAnimate() {
        this.forceAnimate.set(true);
    }
}
