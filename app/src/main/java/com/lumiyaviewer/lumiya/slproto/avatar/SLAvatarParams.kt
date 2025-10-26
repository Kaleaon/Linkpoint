package com.lumiyaviewer.lumiya.slproto.avatar

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.HashMap
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SLAvatarParams {
    Int NUM_PARAMS = 218
    @NonNull
    ImmutableMap<Integer, ParamSet> paramByIDs
    @NonNull
    ParamSet[] paramDefs = ParamSet[218]

    class AvatarParam {
        float defValue
        @Nullable
        ImmutableList<DrivenParam> drivenParams
        float maxValue
        @Nullable
        MeshIndex meshIndex
        float minValue
        Boolean morph
        @Nullable
        SLAvatarParamAlpha paramAlpha
        @Nullable
        SLAvatarParamColor paramColor
        @Nullable
        ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> skeletonParams

        AvatarParam(@Nullable MeshIndex meshIndex2, float f, float f2, float f3, Boolean z, @Nullable SLAvatarParamColor sLAvatarParamColor, @Nullable SLAvatarParamAlpha sLAvatarParamAlpha, @Nullable ImmutableList<DrivenParam> immutableList, @Nullable ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> immutableMap) {
            this.meshIndex = meshIndex2
            this.minValue = f
            this.maxValue = f2
            this.defValue = f3
            this.morph = z
            this.paramColor = sLAvatarParamColor
            this.paramAlpha = sLAvatarParamAlpha
            this.drivenParams = immutableList
            this.skeletonParams = immutableMap
        }
    }

    class DrivenParam {
        Int drivenID
        float max1
        float max2
        float min1
        float min2

        DrivenParam(Int i, float f, float f2, float f3, float f4) {
            this.drivenID = i
            this.min1 = f
            this.max1 = f2
            this.min2 = f3
            this.max2 = f4
        }
    }

    class ParamSet {
        Int appearanceIndex
        Int id
        @NonNull
        SLVisualParamID name
        @NonNull
        ImmutableList<AvatarParam> params

        ParamSet(Int i, Int i2, @NonNull SLVisualParamID sLVisualParamID, @NonNull ImmutableList<AvatarParam> immutableList) {
            this.id = i
            this.appearanceIndex = i2
            this.name = sLVisualParamID
            this.params = immutableList
        }
    }

    class SkeletonParamDefinition {
        @Nullable
        ImmutableVector offset
        @Nullable
        ImmutableVector scale

        SkeletonParamDefinition(@Nullable ImmutableVector immutableVector, @Nullable ImmutableVector immutableVector2) {
            this.scale = immutableVector
            this.offset = immutableVector2
        }
    }

    class SkeletonParamValue {
        @NonNull
        LLVector3 offset
        @NonNull
        LLVector3 scale

        SkeletonParamValue(@NonNull LLVector3 lLVector3, @NonNull LLVector3 lLVector32) {
            this.scale = lLVector3
            this.offset = lLVector32
        }
    }

    {
        HashMap hashMap = HashMap()
        SLAvatarParamBuilder.buildParams(paramDefs, hashMap)
        paramByIDs = ImmutableMap.copyOf(hashMap)
    }
}
