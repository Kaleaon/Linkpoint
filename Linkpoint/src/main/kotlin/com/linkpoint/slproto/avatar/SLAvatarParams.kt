package com.linkpoint.slproto.avatar

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.linkpoint.slproto.types.ImmutableVector
import com.linkpoint.slproto.types.LLVector3
import java.util.HashMap
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLAvatarParams {
    const val NUM_PARAMS: Int = 218
    const val ImmutableMap<Integer, ParamSet> paramByIDs
    const val Array<ParamSet> paramDefs = ParamSet[218]

    @JvmStatic
    class AvatarParam {
        val Float defValue
        val ImmutableList<DrivenParam> drivenParams
        val Float maxValue
        val MeshIndex meshIndex
        val Float minValue
        val Boolean morph
        val SLAvatarParamAlpha paramAlpha
        val SLAvatarParamColor paramColor
        val ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> skeletonParams

        AvatarParam(MeshIndex meshIndex2, Float f, Float f2, Float f3, Boolean z, SLAvatarParamColor sLAvatarParamColor, SLAvatarParamAlpha sLAvatarParamAlpha, ImmutableList<DrivenParam> immutableList, ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> immutableMap) {
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

    @JvmStatic
    class DrivenParam {
        val Int drivenID
        val Float max1
        val Float max2
        val Float min1
        val Float min2

        DrivenParam(Int i, Float f, Float f2, Float f3, Float f4) {
            this.drivenID = i
            this.min1 = f
            this.max1 = f2
            this.min2 = f3
            this.max2 = f4
        }
    }

    @JvmStatic
    class ParamSet {
        val Int appearanceIndex
        val Int id
        val SLVisualParamID name
        val ImmutableList<AvatarParam> params

        ParamSet(Int i, Int i2, SLVisualParamID sLVisualParamID, ImmutableList<AvatarParam> immutableList) {
            this.id = i
            this.appearanceIndex = i2
            this.name = sLVisualParamID
            this.params = immutableList
        }
    }

    @JvmStatic
    class SkeletonParamDefinition {
        val ImmutableVector offset
        val ImmutableVector scale

        SkeletonParamDefinition(ImmutableVector immutableVector, ImmutableVector immutableVector2) {
            this.scale = immutableVector
            this.offset = immutableVector2
        }
    }

    @JvmStatic
    class SkeletonParamValue {
        val LLVector3 offset
        val LLVector3 scale

        public SkeletonParamValue(LLVector3 lLVector3, LLVector3 lLVector32) {
            this.scale = lLVector3
            this.offset = lLVector32
        }
    }

    static {
        val hashMap: HashMap = HashMap()
        SLAvatarParamBuilder.buildParams(paramDefs, hashMap)
        paramByIDs = ImmutableMap.copyOf(hashMap)
    }
}
