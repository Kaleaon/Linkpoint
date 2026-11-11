package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import com.lumiyaviewer.lumiya.slproto.avatar.SLDefaultSkeleton;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.lumiya.slproto.avatar.SLVisualParamID;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class AvatarSkeleton extends SLDefaultSkeleton {
    private final float bodySize;
    private final boolean hasExtendedBones;
    private final float pelvisOffset;
    private final float pelvisToFoot;
    private final Map<MeshIndex, float[]> partMorphParams = new EnumMap(MeshIndex.class);
    private final AttachmentPoint[] attachmentPoints = new AttachmentPoint[56];
    private final AtomicBoolean forceAnimate = new AtomicBoolean(true);
    private final SLSkeletonBone[] animatedBones = new SLSkeletonBone[133];

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AttachmentPoint {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public AvatarSkeleton(@Nonnull AvatarShapeParams avatarShapeParams, @Nonnull MeshJointTranslations meshJointTranslations, boolean z) {
        MeshIndex[] meshIndexArr;
        SLSkeletonBoneID[] sLSkeletonBoneIDArr;
        SLSkeletonBoneID[] sLSkeletonBoneIDArr2;
        this.hasExtendedBones = z;
        prepareSkeleton();
        Iterator<T> it = this.bones.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            int i = ((SLSkeletonBoneID) entry.getKey()).animatedIndex;
            if (i >= 0 && i < 133) {
                this.animatedBones[i] = (SLSkeletonBone) entry.getValue();
            }
        }
        EnumMap enumMap = new EnumMap(SLSkeletonBoneID.class);
        SLBaseAvatar sLBaseAvatar = SLBaseAvatar.getInstance();
        applyJointTranslations(meshJointTranslations);
        this.pelvisOffset = meshJointTranslations.pelvisOffset;
        for (MeshIndex meshIndex : MeshIndex.VALUES) {
            float[] fArr = new float[sLBaseAvatar.getMeshEntry(meshIndex).polyMesh.getNumMorphs()];
            Arrays.fill(fArr, 0.0f);
            this.partMorphParams.put(meshIndex, fArr);
        }
        for (SLSkeletonBoneID sLSkeletonBoneID : SLSkeletonBoneID.VALUES) {
            enumMap.put((EnumMap) sLSkeletonBoneID, (SLSkeletonBoneID) new SLAvatarParams.SkeletonParamValue(new LLVector3(), new LLVector3()));
            enumMap.get(sLSkeletonBoneID).scale.set(1.0f, 1.0f, 1.0f);
            enumMap.get(sLSkeletonBoneID).offset.set(0.0f, 0.0f, 0.0f);
        }
        int paramCount = avatarShapeParams.getParamCount();
        for (int i2 = 0; i2 < paramCount; i2++) {
            SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramDefs[i2];
            for (SLAvatarParams.AvatarParam avatarParam : paramSet.params) {
                float paramValue = ((avatarShapeParams.getParamValue(i2) * (avatarParam.maxValue - avatarParam.minValue)) / 255.0f) + avatarParam.minValue;
                ApplyMorphParam(sLBaseAvatar, enumMap, avatarParam, paramSet.name, paramValue);
                if (avatarParam.drivenParams != null) {
                    for (SLAvatarParams.DrivenParam drivenParam : avatarParam.drivenParams) {
                        SLAvatarParams.ParamSet paramSet2 = SLAvatarParams.paramByIDs.get(Integer.valueOf(drivenParam.drivenID));
                        if (paramSet2 != null) {
                            for (SLAvatarParams.AvatarParam avatarParam2 : paramSet2.params) {
                                ApplyMorphParam(sLBaseAvatar, enumMap, avatarParam2, paramSet2.name, getDrivenWeight(paramValue, avatarParam, drivenParam, avatarParam2));
                            }
                        }
                    }
                }
            }
        }
        for (SLSkeletonBoneID sLSkeletonBoneID2 : SLSkeletonBoneID.VALUES) {
            this.bones.get(sLSkeletonBoneID2).deformHierarchy(enumMap.get(sLSkeletonBoneID2).offset, enumMap.get(sLSkeletonBoneID2).scale);
        }
        this.pelvisToFoot = super.getPelvisToFoot();
        this.bodySize = super.getBodySize();
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= 56) {
                updateAttachmentMatrix();
                return;
            }
            SLAttachmentPoint sLAttachmentPoint = SLAttachmentPoint.attachmentPoints[i4];
            if (sLAttachmentPoint != null && !sLAttachmentPoint.isHUD) {
                SLSkeletonBoneID sLSkeletonBoneID3 = sLAttachmentPoint.bone;
                if (sLSkeletonBoneID3 != null) {
                    SLSkeletonBone sLSkeletonBone = this.bones.get(sLSkeletonBoneID3);
                    if (sLSkeletonBone != null) {
                        this.attachmentPoints[i4] = new AttachmentPoint(sLSkeletonBone, sLAttachmentPoint, null);
                    }
                } else {
                    this.attachmentPoints[i4] = new AttachmentPoint(null, sLAttachmentPoint, null);
                }
            }
            i3 = i4 + 1;
        }
    }

    private void ApplyMorphParam(SLBaseAvatar sLBaseAvatar, Map<SLSkeletonBoneID, SLAvatarParams.SkeletonParamValue> map, SLAvatarParams.AvatarParam avatarParam, SLVisualParamID sLVisualParamID, float f) {
        float[] fArr;
        int morphIndex;
        if (avatarParam.morph && avatarParam.meshIndex != null && (fArr = this.partMorphParams.get(avatarParam.meshIndex)) != null && (morphIndex = sLBaseAvatar.getMeshEntry(avatarParam.meshIndex).polyMesh.getMorphIndex(sLVisualParamID)) != -1) {
            fArr[morphIndex] = fArr[morphIndex] + f;
        }
        if (avatarParam.skeletonParams != null) {
            for (Map.Entry<SLSkeletonBoneID, SLAvatarParams.SkeletonParamDefinition> entry : avatarParam.skeletonParams.entrySet()) {
                SLAvatarParams.SkeletonParamValue skeletonParamValue = map.get(entry.getKey());
                SLAvatarParams.SkeletonParamDefinition value = entry.getValue();
                if (value.scale != null) {
                    skeletonParamValue.scale.mulWeighted(value.scale, f);
                }
                if (value.offset != null) {
                    skeletonParamValue.offset.addMul(value.offset, f);
                }
            }
        }
    }

    public static float getDrivenWeight(float f, SLAvatarParams.AvatarParam avatarParam, SLAvatarParams.DrivenParam drivenParam, SLAvatarParams.AvatarParam avatarParam2) {
        float f2 = avatarParam.minValue;
        float f3 = avatarParam.maxValue;
        float f4 = avatarParam2.minValue;
        float f5 = avatarParam2.maxValue;
        if (f <= drivenParam.min1) {
            return (drivenParam.min1 != drivenParam.max1 || drivenParam.min1 > f2) ? f4 : f5;
        } else if (f <= drivenParam.max1) {
            return ((f5 - f4) * ((f - drivenParam.min1) / (drivenParam.max1 - drivenParam.min1))) + f4;
        } else if (f > drivenParam.max2) {
            if (f <= drivenParam.min2) {
                return f5 + ((f4 - f5) * ((f - drivenParam.max2) / (drivenParam.min2 - drivenParam.max2)));
            }
            return drivenParam.max2 < f3 ? f4 : f5;
        } else {
            return f5;
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

    @Override // com.lumiyaviewer.lumiya.slproto.avatar.SLSkeleton
    public void UpdateGlobalPositions(AnimationSkeletonData animationSkeletonData) {
        super.UpdateGlobalPositions(animationSkeletonData);
        updateAttachmentMatrix();
    }

    public SLSkeletonBone getAnimatedBone(int i) {
        return this.animatedBones[i];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final float[] getAttachmentMatrix(int i) {
        AttachmentPoint attachmentPoint;
        if (i < 0 || i >= this.attachmentPoints.length || (attachmentPoint = this.attachmentPoints[i]) == null) {
            return null;
        }
        return attachmentPoint.matrix;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.avatar.SLSkeleton
    public final float getBodySize() {
        return this.bodySize;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final float[] getMorphParams(MeshIndex meshIndex) {
        return this.partMorphParams.get(meshIndex);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final float getPelvisOffset() {
        return this.pelvisOffset;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.avatar.SLSkeleton
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
