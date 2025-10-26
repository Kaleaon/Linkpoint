package com.linkpoint.slproto.avatar

import android.content.res.AssetManager
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import com.linkpoint.slproto.avatar.SLAvatarParamColor
import com.linkpoint.slproto.avatar.SLAvatarParams
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.types.ImmutableVector
import java.io.IOException
import java.io.InputStream
import java.util.EnumMap
import java.util.Map
import javax.annotation.Nullable

class SLAvatarParamBuilder {
    SLAvatarParamBuilder() {
    }

    static Unit buildParams(SLAvatarParams.Array<ParamSet> paramSetArr, Map<Integer, SLAvatarParams.ParamSet> map) {
        try {
            val assetManager: AssetManager = LinkpointApp.getAssetManager()
            if (assetManager != null) {
                val open: InputStream = assetManager.open("character/avatar_params.xml", 3)
                val parseXML: LLSDNode = LLSDNode.parseXML(open, "UTF-8")
                val count: Int = parseXML.getCount()
                for (Int i = 0; i < count; i++) {
                    val byIndex: LLSDNode = parseXML.byIndex(i)
                    val byKey: LLSDNode = byIndex.byKey("params")
                    val count2: Int = byKey.getCount()
                    ImmutableList.Builder builder = ImmutableList.builder()
                    for (Int i2 = 0; i2 < count2; i2++) {
                        val byIndex2: LLSDNode = byKey.byIndex(i2)
                        val sLAvatarParamColor: SLAvatarParamColor = null
                        val sLAvatarParamAlpha: SLAvatarParamAlpha = null
                        if (byIndex2.keyExists("paramColor")) {
                            val byKey2: LLSDNode = byIndex2.byKey("paramColor")
                            val byKey3: LLSDNode = byKey2.byKey("values")
                            val iArr: IntArray = Int[byKey3.getCount()]
                            for (Int i3 = 0; i3 < iArr.length; i3++) {
                                iArr[i3] = byKey3.byIndex(i3).asInt()
                            }
                            sLAvatarParamColor = SLAvatarParamColor(SLAvatarParamColor.ColorOperation.valueOf(byKey2.byKey("opcode").asString()), iArr)
                        }
                        if (byIndex2.keyExists("paramAlpha")) {
                            val byKey4: LLSDNode = byIndex2.byKey("paramAlpha")
                            sLAvatarParamAlpha = SLAvatarParamAlpha((Float) byKey4.byKey("domain").asDouble(), byKey4.keyExists("tgaFile") ? byKey4.byKey("tgaFile").asString() : null, byKey4.byKey("skipIfZero").asBoolean(), byKey4.byKey("multiplyBlend").asBoolean())
                        }
                        val valueOf: MeshIndex = byIndex2.keyExists("meshIndex") ? MeshIndex.valueOf(byIndex2.byKey("meshIndex").asString()) : null
                        val immutableList: ImmutableList = null
                        if (byIndex2.keyExists("driven")) {
                            ImmutableList.Builder builder2 = ImmutableList.builder()
                            val byKey5: LLSDNode = byIndex2.byKey("driven")
                            for (Int i4 = 0; i4 < byKey5.getCount(); i4++) {
                                val byIndex3: LLSDNode = byKey5.byIndex(i4)
                                builder2.add((Object) SLAvatarParams.DrivenParam(byIndex3.byKey("driven_id").asInt(), (Float) byIndex3.byKey("min1").asDouble(), (Float) byIndex3.byKey("max1").asDouble(), (Float) byIndex3.byKey("min2").asDouble(), (Float) byIndex3.byKey("max2").asDouble()))
                            }
                            immutableList = builder2.build()
                        }
                        val immutableMap: ImmutableMap = null
                        if (byIndex2.keyExists("skeleton")) {
                            val enumMap: EnumMap = EnumMap(SLSkeletonBoneID.class)
                            val byKey6: LLSDNode = byIndex2.byKey("skeleton")
                            for (Int i5 = 0; i5 < byKey6.getCount(); i5++) {
                                val byIndex4: LLSDNode = byKey6.byIndex(i5)
                                val sLSkeletonBoneID: SLSkeletonBoneID = SLSkeletonBoneID.bones.get(byIndex4.byKey("bone_id").asString())
                                if (sLSkeletonBoneID != null) {
                                    enumMap.put(sLSkeletonBoneID, SLAvatarParams.SkeletonParamDefinition(vectorFromNode(byIndex4, "scale"), vectorFromNode(byIndex4, "offset")))
                                }
                            }
                            immutableMap = Maps.immutableEnumMap(enumMap)
                        }
                        builder.add((Object) SLAvatarParams.AvatarParam(valueOf, (Float) byIndex2.byKey("minValue").asDouble(), (Float) byIndex2.byKey("maxValue").asDouble(), (Float) byIndex2.byKey("defValue").asDouble(), byIndex2.byKey("morph").asBoolean(), sLAvatarParamColor, sLAvatarParamAlpha, immutableList, immutableMap))
                    }
                    SLAvatarParams.ParamSet paramSet = SLAvatarParams.ParamSet(byIndex.byKey("setId").asInt(), byIndex.byKey("appearanceIndex").asInt(), SLVisualParamID.valueOf(byIndex.byKey("setName").asString()), builder.build())
                    map.put(Integer.valueOf(paramSet.id), paramSet)
                    if (paramSet.appearanceIndex != -1) {
                        paramSetArr[paramSet.appearanceIndex] = paramSet
                    }
                }
                open.close()
            }
        } catch (IOException e) {
            Debug.Warning(e)
        } catch (LLSDException e2) {
            Debug.Warning(e2)
        }
    }

    @JvmStatic
 private fun vectorFromNode(lLSDNode: LLSDNode, str: String) throws LLSDException {
        if (!lLSDNode.keyExists(str)) {
            return null
        }
        val byKey: LLSDNode = lLSDNode.byKey(str)
        return ImmutableVector((Float) byKey.byKey("x").asDouble(), (Float) byKey.byKey("y").asDouble(), (Float) byKey.byKey("z").asDouble())
    }
}
