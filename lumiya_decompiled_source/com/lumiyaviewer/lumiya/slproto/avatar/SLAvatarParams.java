package com.lumiyaviewer.lumiya.slproto.avatar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SLAvatarParams {
    public static final int NUM_PARAMS = 218;
    @Nonnull
    public static final ImmutableMap<Integer, ParamSet> paramByIDs;
    @Nonnull
    public static final ParamSet[] paramDefs = new ParamSet[218];

    /* loaded from: classes.dex */
    public static class AvatarParam {
        public final float defValue;
        @Nullable
        public final ImmutableList<DrivenParam> drivenParams;
        public final float maxValue;
        @Nullable
        public final MeshIndex meshIndex;
        public final float minValue;
        public final boolean morph;
        @Nullable
        public final SLAvatarParamAlpha paramAlpha;
        @Nullable
        public final SLAvatarParamColor paramColor;
        @Nullable
        public final ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> skeletonParams;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AvatarParam(@Nullable MeshIndex meshIndex, float f, float f2, float f3, boolean z, @Nullable SLAvatarParamColor sLAvatarParamColor, @Nullable SLAvatarParamAlpha sLAvatarParamAlpha, @Nullable ImmutableList<DrivenParam> immutableList, @Nullable ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> immutableMap) {
            this.meshIndex = meshIndex;
            this.minValue = f;
            this.maxValue = f2;
            this.defValue = f3;
            this.morph = z;
            this.paramColor = sLAvatarParamColor;
            this.paramAlpha = sLAvatarParamAlpha;
            this.drivenParams = immutableList;
            this.skeletonParams = immutableMap;
        }
    }

    /* loaded from: classes.dex */
    public static class DrivenParam {
        public final int drivenID;
        public final float max1;
        public final float max2;
        public final float min1;
        public final float min2;

        /* JADX INFO: Access modifiers changed from: package-private */
        public DrivenParam(int i, float f, float f2, float f3, float f4) {
            this.drivenID = i;
            this.min1 = f;
            this.max1 = f2;
            this.min2 = f3;
            this.max2 = f4;
        }
    }

    /* loaded from: classes.dex */
    public static class ParamSet {
        public final int appearanceIndex;
        public final int id;
        @Nonnull
        public final SLVisualParamID name;
        @Nonnull
        public final ImmutableList<AvatarParam> params;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ParamSet(int i, int i2, @Nonnull SLVisualParamID sLVisualParamID, @Nonnull ImmutableList<AvatarParam> immutableList) {
            this.id = i;
            this.appearanceIndex = i2;
            this.name = sLVisualParamID;
            this.params = immutableList;
        }
    }

    /* loaded from: classes.dex */
    public static class SkeletonParamDefinition {
        @Nullable
        public final ImmutableVector offset;
        @Nullable
        public final ImmutableVector scale;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SkeletonParamDefinition(@Nullable ImmutableVector immutableVector, @Nullable ImmutableVector immutableVector2) {
            this.scale = immutableVector;
            this.offset = immutableVector2;
        }
    }

    /* loaded from: classes.dex */
    public static class SkeletonParamValue {
        @Nonnull
        public final LLVector3 offset;
        @Nonnull
        public final LLVector3 scale;

        public SkeletonParamValue(@Nonnull LLVector3 lLVector3, @Nonnull LLVector3 lLVector32) {
            this.scale = lLVector3;
            this.offset = lLVector32;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        SLAvatarParamBuilder.buildParams(paramDefs, hashMap);
        paramByIDs = ImmutableMap.copyOf((Map) hashMap);
    }
}
