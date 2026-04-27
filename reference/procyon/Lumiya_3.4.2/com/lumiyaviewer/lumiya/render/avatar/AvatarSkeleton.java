// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.avatar.SLVisualParamID;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.Arrays;
import com.lumiyaviewer.lumiya.slproto.avatar.SLBaseAvatar;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import java.util.EnumMap;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.avatar.MeshIndex;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;
import com.lumiyaviewer.lumiya.slproto.avatar.SLDefaultSkeleton;

public class AvatarSkeleton extends SLDefaultSkeleton
{
    private final SLSkeletonBone[] animatedBones;
    private final AttachmentPoint[] attachmentPoints;
    private final float bodySize;
    private final AtomicBoolean forceAnimate;
    private final boolean hasExtendedBones;
    private final Map<MeshIndex, float[]> partMorphParams;
    private final float pelvisOffset;
    private final float pelvisToFoot;
    
    AvatarSkeleton(@Nonnull final AvatarShapeParams avatarShapeParams, @Nonnull final MeshJointTranslations meshJointTranslations, final boolean hasExtendedBones) {
        this.partMorphParams = new EnumMap<MeshIndex, float[]>(MeshIndex.class);
        this.attachmentPoints = new AttachmentPoint[56];
        this.forceAnimate = new AtomicBoolean(true);
        this.animatedBones = new SLSkeletonBone[133];
        this.hasExtendedBones = hasExtendedBones;
        this.prepareSkeleton();
        for (final Map.Entry<SLSkeletonBoneID, V> entry : this.bones.entrySet()) {
            final int animatedIndex = entry.getKey().animatedIndex;
            if (animatedIndex >= 0 && animatedIndex < 133) {
                this.animatedBones[animatedIndex] = (SLSkeletonBone)entry.getValue();
            }
        }
        final EnumMap enumMap = new EnumMap(SLSkeletonBoneID.class);
        final SLBaseAvatar instance = SLBaseAvatar.getInstance();
        this.applyJointTranslations(meshJointTranslations);
        this.pelvisOffset = meshJointTranslations.pelvisOffset;
        final MeshIndex[] values = MeshIndex.VALUES;
        for (int i = 0; i < values.length; ++i) {
            final MeshIndex meshIndex = values[i];
            final float[] a = new float[instance.getMeshEntry(meshIndex).polyMesh.getNumMorphs()];
            Arrays.fill(a, 0.0f);
            this.partMorphParams.put(meshIndex, a);
        }
        for (final SLSkeletonBoneID slSkeletonBoneID : SLSkeletonBoneID.VALUES) {
            enumMap.put(slSkeletonBoneID, new SLAvatarParams.SkeletonParamValue(new LLVector3(), new LLVector3()));
            ((SLAvatarParams.SkeletonParamValue)enumMap.get(slSkeletonBoneID)).scale.set(1.0f, 1.0f, 1.0f);
            ((SLAvatarParams.SkeletonParamValue)enumMap.get(slSkeletonBoneID)).offset.set(0.0f, 0.0f, 0.0f);
        }
        for (int paramCount = avatarShapeParams.getParamCount(), k = 0; k < paramCount; ++k) {
            final SLAvatarParams.ParamSet set = SLAvatarParams.paramDefs[k];
            for (final SLAvatarParams.AvatarParam avatarParam : set.params) {
                final float n = avatarShapeParams.getParamValue(k) * (avatarParam.maxValue - avatarParam.minValue) / 255.0f + avatarParam.minValue;
                this.ApplyMorphParam(instance, enumMap, avatarParam, set.name, n);
                if (avatarParam.drivenParams != null) {
                    for (final SLAvatarParams.DrivenParam drivenParam : avatarParam.drivenParams) {
                        final SLAvatarParams.ParamSet set2 = SLAvatarParams.paramByIDs.get(drivenParam.drivenID);
                        if (set2 != null) {
                            for (final SLAvatarParams.AvatarParam avatarParam2 : set2.params) {
                                this.ApplyMorphParam(instance, enumMap, avatarParam2, set2.name, getDrivenWeight(n, avatarParam, drivenParam, avatarParam2));
                            }
                        }
                    }
                }
            }
        }
        for (final SLSkeletonBoneID slSkeletonBoneID2 : SLSkeletonBoneID.VALUES) {
            this.bones.get(slSkeletonBoneID2).deformHierarchy(((SLAvatarParams.SkeletonParamValue)enumMap.get(slSkeletonBoneID2)).offset, ((SLAvatarParams.SkeletonParamValue)enumMap.get(slSkeletonBoneID2)).scale);
        }
        this.pelvisToFoot = super.getPelvisToFoot();
        this.bodySize = super.getBodySize();
        for (int n2 = 0; n2 < 56; ++n2) {
            final SLAttachmentPoint slAttachmentPoint = SLAttachmentPoint.attachmentPoints[n2];
            if (slAttachmentPoint != null && !slAttachmentPoint.isHUD) {
                final SLSkeletonBoneID bone = slAttachmentPoint.bone;
                if (bone != null) {
                    final SLSkeletonBone slSkeletonBone = this.bones.get(bone);
                    if (slSkeletonBone != null) {
                        this.attachmentPoints[n2] = new AttachmentPoint(slSkeletonBone, slAttachmentPoint, null);
                    }
                }
                else {
                    this.attachmentPoints[n2] = new AttachmentPoint(null, slAttachmentPoint, null);
                }
            }
        }
        this.updateAttachmentMatrix();
    }
    
    private void ApplyMorphParam(final SLBaseAvatar slBaseAvatar, final Map<SLSkeletonBoneID, SLAvatarParams.SkeletonParamValue> map, final SLAvatarParams.AvatarParam avatarParam, final SLVisualParamID slVisualParamID, final float n) {
        if (avatarParam.morph && avatarParam.meshIndex != null) {
            final float[] array = this.partMorphParams.get(avatarParam.meshIndex);
            if (array != null) {
                final int morphIndex = slBaseAvatar.getMeshEntry(avatarParam.meshIndex).polyMesh.getMorphIndex(slVisualParamID);
                if (morphIndex != -1) {
                    array[morphIndex] += n;
                }
            }
        }
        if (avatarParam.skeletonParams != null) {
            for (final Map.Entry<Object, V> entry : avatarParam.skeletonParams.entrySet()) {
                final SLAvatarParams.SkeletonParamValue skeletonParamValue = map.get(entry.getKey());
                final SLAvatarParams.SkeletonParamDefinition skeletonParamDefinition = (SLAvatarParams.SkeletonParamDefinition)entry.getValue();
                if (skeletonParamDefinition.scale != null) {
                    skeletonParamValue.scale.mulWeighted(skeletonParamDefinition.scale, n);
                }
                if (skeletonParamDefinition.offset != null) {
                    skeletonParamValue.offset.addMul(skeletonParamDefinition.offset, n);
                }
            }
        }
    }
    
    public static float getDrivenWeight(final float n, final SLAvatarParams.AvatarParam avatarParam, final SLAvatarParams.DrivenParam drivenParam, final SLAvatarParams.AvatarParam avatarParam2) {
        final float minValue = avatarParam.minValue;
        final float maxValue = avatarParam.maxValue;
        final float minValue2 = avatarParam2.minValue;
        final float maxValue2 = avatarParam2.maxValue;
        float n2;
        if (n <= drivenParam.min1) {
            if (drivenParam.min1 == drivenParam.max1 && drivenParam.min1 <= minValue) {
                n2 = maxValue2;
            }
            else {
                n2 = minValue2;
            }
        }
        else if (n <= drivenParam.max1) {
            n2 = (maxValue2 - minValue2) * ((n - drivenParam.min1) / (drivenParam.max1 - drivenParam.min1)) + minValue2;
        }
        else {
            n2 = maxValue2;
            if (n > drivenParam.max2) {
                if (n <= drivenParam.min2) {
                    n2 = maxValue2 + (minValue2 - maxValue2) * ((n - drivenParam.max2) / (drivenParam.min2 - drivenParam.max2));
                }
                else {
                    n2 = maxValue2;
                    if (drivenParam.max2 < maxValue) {
                        n2 = minValue2;
                    }
                }
            }
        }
        return n2;
    }
    
    private void updateAttachmentMatrix() {
        final float[] array = new float[16];
        for (int i = 0; i < 56; ++i) {
            final AttachmentPoint attachmentPoint = this.attachmentPoints[i];
            if (attachmentPoint != null) {
                final SLSkeletonBone bone = attachmentPoint.bone;
                if (bone != null) {
                    Matrix.translateM(array, 0, bone.getGlobalMatrix(), 0, attachmentPoint.point.position.x * bone.getScaleX(), attachmentPoint.point.position.y * bone.getScaleY(), attachmentPoint.point.position.z * bone.getScaleZ());
                    Matrix.multiplyMM(attachmentPoint.matrix, 0, array, 0, attachmentPoint.point.rotation.getInverseMatrix(), 0);
                }
                else {
                    Matrix.setIdentityM(array, 0);
                    Matrix.translateM(array, 0, this.rootBone.getPositionX(), this.rootBone.getPositionY(), this.rootBone.getPositionZ());
                    Matrix.translateM(array, 0, attachmentPoint.point.position.x, attachmentPoint.point.position.y, attachmentPoint.point.position.z);
                    Matrix.multiplyMM(attachmentPoint.matrix, 0, array, 0, attachmentPoint.point.rotation.getInverseMatrix(), 0);
                }
                final int nonHUDindex = SLAttachmentPoint.attachmentPoints[i].nonHUDindex;
                if (nonHUDindex >= 0) {
                    System.arraycopy(attachmentPoint.matrix, 0, this.jointWorldMatrix, (nonHUDindex + SLSkeletonBoneID.VALUES.length) * 16, 16);
                }
            }
        }
    }
    
    @Override
    public void UpdateGlobalPositions(final AnimationSkeletonData animationSkeletonData) {
        super.UpdateGlobalPositions(animationSkeletonData);
        this.updateAttachmentMatrix();
    }
    
    public SLSkeletonBone getAnimatedBone(final int n) {
        return this.animatedBones[n];
    }
    
    final float[] getAttachmentMatrix(final int n) {
        if (n >= 0 && n < this.attachmentPoints.length) {
            final AttachmentPoint attachmentPoint = this.attachmentPoints[n];
            if (attachmentPoint != null) {
                return attachmentPoint.matrix;
            }
        }
        return null;
    }
    
    @Override
    public final float getBodySize() {
        return this.bodySize;
    }
    
    final float[] getMorphParams(final MeshIndex meshIndex) {
        return this.partMorphParams.get(meshIndex);
    }
    
    final float getPelvisOffset() {
        return this.pelvisOffset;
    }
    
    @Override
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
    
    private static class AttachmentPoint
    {
        final SLSkeletonBone bone;
        public final float[] matrix;
        public final SLAttachmentPoint point;
        
        private AttachmentPoint(final SLSkeletonBone bone, final SLAttachmentPoint point) {
            this.matrix = new float[16];
            this.bone = bone;
            this.point = point;
        }
    }
}
