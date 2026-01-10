package com.linkpoint.slproto.avatar

import android.content.res.AssetManager
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.linkpoint.Debug
import com.linkpoint.LumiyaApp
import com.linkpoint.slproto.avatar.SLAvatarParamColor
import com.linkpoint.slproto.avatar.SLAvatarParams
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.types.ImmutableVector
import java.io.IOException
import java.io.InputStream
import java.util.EnumMap
import java.util.Map
import androidx.annotation.Nullable

class SLAvatarParamBuilder {
    SLAvatarParamBuilder() {
    }

    fun buildParams(SLAvatarParams.ParamSet[] paramSetArr, Map<Integer, SLAvatarParams.ParamSet> map)  {
        try {
            AssetManager assetManager = LumiyaApp.getAssetManager()
            if (assetManager != null) {
                InputStream open = assetManager.open("character/avatar_params.xml", 3)
                LLSDNode parseXML = LLSDNode.parseXML(open, "UTF-8")
                var count: Int = parseXML.getCount()
                for (i in 0 until count) {
                    LLSDNode byIndex = parseXML.byIndex(i)
                    LLSDNode byKey = byIndex.byKey("params")
                    var count2: Int = byKey.getCount()
                    ImmutableList.Builder builder = ImmutableList.builder()
                    for (i2 in 0 until count2) {
                        LLSDNode byIndex2 = byKey.byIndex(i2)
                        SLAvatarParamColor sLAvatarParamColor = null
                        SLAvatarParamAlpha sLAvatarParamAlpha = null
                        if (byIndex2.keyExists("paramColor")) {
                            LLSDNode byKey2 = byIndex2.byKey("paramColor")
                            LLSDNode byKey3 = byKey2.byKey("values")
                            IntArray iArr = Int[byKey3.getCount()]
                            for (i3 in 0 until iArr.size) {
                                iArr[i3] = byKey3.byIndex(i3).asInt()
                            }
                            sLAvatarParamColor = SLAvatarParamColor(SLAvatarParamColor.ColorOperation.valueOf(byKey2.byKey("opcode").asString()), iArr)
                        }
                        if (byIndex2.keyExists("paramAlpha")) {
                            LLSDNode byKey4 = byIndex2.byKey("paramAlpha")
                            sLAvatarParamAlpha = SLAvatarParamAlpha((byKey4 as float).byKey("domain").asDouble(), byKey4.keyExists("tgaFile") ? byKey4.byKey("tgaFile").asString() : null, byKey4.byKey("skipIfZero").asBoolean(), byKey4.byKey("multiplyBlend").asBoolean())
                        }
                        MeshIndex valueOf = byIndex2.keyExists("meshIndex") ? MeshIndex.valueOf(byIndex2.byKey("meshIndex").asString()) : null
                        ImmutableList immutableList = null
                        if (byIndex2.keyExists("driven")) {
                            ImmutableList.Builder builder2 = ImmutableList.builder()
                            LLSDNode byKey5 = byIndex2.byKey("driven")
                            for (i4 in 0 until byKey5.getCount()) {
                                LLSDNode byIndex3 = byKey5.byIndex(i4)
                                builder2.add((SLAvatarParams as Object).DrivenParam(byIndex3.byKey("driven_id").asInt(), (byIndex3 as float).byKey("min1").asDouble(), (byIndex3 as float).byKey("max1").asDouble(), (byIndex3 as float).byKey("min2").asDouble(), (byIndex3 as float).byKey("max2").asDouble()))
                            }
                            immutableList = builder2.build()
                        }
                        ImmutableMap immutableMap = null
                        if (byIndex2.keyExists("skeleton")) {
                            EnumMap enumMap = EnumMap(SLSkeletonBoneID.class)
                            LLSDNode byKey6 = byIndex2.byKey("skeleton")
                            for (i5 in 0 until byKey6.getCount()) {
                                LLSDNode byIndex4 = byKey6.byIndex(i5)
                                SLSkeletonBoneID sLSkeletonBoneID = SLSkeletonBoneID.bones.get(byIndex4.byKey("bone_id").asString())
                                if (sLSkeletonBoneID != null) {
                                    enumMap.put(sLSkeletonBoneID, SLAvatarParams.SkeletonParamDefinition(vectorFromNode(byIndex4, "scale"), vectorFromNode(byIndex4, "offset")))
                                }
                            }
                            immutableMap = Maps.immutableEnumMap(enumMap)
                        }
                        builder.add((SLAvatarParams as Object).AvatarParam(valueOf, (byIndex2 as float).byKey("minValue").asDouble(), (byIndex2 as float).byKey("maxValue").asDouble(), (byIndex2 as float).byKey("defValue").asDouble(), byIndex2.byKey("morph").asBoolean(), sLAvatarParamColor, sLAvatarParamAlpha, immutableList, immutableMap))
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

    @Nullable
    private ImmutableVector vectorFromNode(LLSDNode lLSDNode, String str) throws LLSDException {
        if (!lLSDNode.keyExists(str)) {
            return null
        }
        LLSDNode byKey = lLSDNode.byKey(str)
        return ImmutableVector((byKey as float).byKey("x").asDouble(), (byKey as float).byKey("y").asDouble(), (byKey as float).byKey("z").asDouble())
    }
}
