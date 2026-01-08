package com.lumiyaviewer.lumiya.slproto.avatar

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.HashMap

class SLAvatarParams {
    val NUM_PARAMS = 218
    val paramByIDs: ImmutableMap<Int, ParamSet>
    val paramDefs = arrayOfNulls<ParamSet>(218)

    class AvatarParam(
        val meshIndex: MeshIndex?,
        val minValue: Float,
        val maxValue: Float,
        val defValue: Float,
        val morph: Boolean?,
        val paramColor: SLAvatarParamColor?,
        val paramAlpha: SLAvatarParamAlpha?,
        val drivenParams: ImmutableList<DrivenParam>?,
        val skeletonParams: ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition>?
    )

    class DrivenParam(
        val drivenID: Int,
        val min1: Float,
        val max1: Float,
        val min2: Float,
        val max2: Float
    )

    class ParamSet(
        val id: Int,
        val appearanceIndex: Int,
        val name: SLVisualParamID,
        val params: ImmutableList<AvatarParam>
    )

    class SkeletonParamDefinition(
        val scale: ImmutableVector?,
        val offset: ImmutableVector?
    )

    class SkeletonParamValue(
        val scale: LLVector3,
        val offset: LLVector3
    )

    init {
        val hashMap = HashMap<Int, ParamSet>()
        SLAvatarParamBuilder.buildParams(paramDefs, hashMap)
        paramByIDs = ImmutableMap.copyOf(hashMap)
    }
    
    companion object {
        private val instance = SLAvatarParams()
        fun getInstance(): SLAvatarParams = instance
    }
}
