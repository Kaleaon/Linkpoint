package com.lumiyaviewer.lumiya.slproto.avatar;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParamColor;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;

class SLAvatarParamBuilder {
    SLAvatarParamBuilder() {
    }

    static void buildParams(SLAvatarParams.ParamSet[] paramSetArr, Map<Integer, SLAvatarParams.ParamSet> map) {
        try {
            AssetManager assetManager = LumiyaApp.getAssetManager();
            if (assetManager != null) {
                InputStream open = assetManager.open("character/avatar_params.xml", 3);
                LLSDNode parseXML = LLSDNode.parseXML(open, "UTF-8");
                int count = parseXML.getCount();
                for (int i = 0; i < count; i++) {
                    LLSDNode byIndex = parseXML.byIndex(i);
                    LLSDNode byKey = byIndex.byKey("params");
                    int count2 = byKey.getCount();
                    ImmutableList.Builder builder = ImmutableList.builder();
                    for (int i2 = 0; i2 < count2; i2++) {
                        LLSDNode byIndex2 = byKey.byIndex(i2);
                        SLAvatarParamColor sLAvatarParamColor = null;
                        SLAvatarParamAlpha sLAvatarParamAlpha = null;
                        if (byIndex2.keyExists("paramColor")) {
                            LLSDNode byKey2 = byIndex2.byKey("paramColor");
                            LLSDNode byKey3 = byKey2.byKey("values");
                            int[] iArr = new int[byKey3.getCount()];
                            for (int i3 = 0; i3 < iArr.length; i3++) {
                                iArr[i3] = byKey3.byIndex(i3).asInt();
                            }
                            sLAvatarParamColor = new SLAvatarParamColor(SLAvatarParamColor.ColorOperation.valueOf(byKey2.byKey("opcode").asString()), iArr);
                        }
                        if (byIndex2.keyExists("paramAlpha")) {
                            LLSDNode byKey4 = byIndex2.byKey("paramAlpha");
                            sLAvatarParamAlpha = new SLAvatarParamAlpha((float) byKey4.byKey("domain").asDouble(), byKey4.keyExists("tgaFile") ? byKey4.byKey("tgaFile").asString() : null, byKey4.byKey("skipIfZero").asBoolean(), byKey4.byKey("multiplyBlend").asBoolean());
                        }
                        MeshIndex valueOf = byIndex2.keyExists("meshIndex") ? MeshIndex.valueOf(byIndex2.byKey("meshIndex").asString()) : null;
                        ImmutableList immutableList = null;
                        if (byIndex2.keyExists("driven")) {
                            ImmutableList.Builder builder2 = ImmutableList.builder();
                            LLSDNode byKey5 = byIndex2.byKey("driven");
                            for (int i4 = 0; i4 < byKey5.getCount(); i4++) {
                                LLSDNode byIndex3 = byKey5.byIndex(i4);
                                builder2.add((Object) new SLAvatarParams.DrivenParam(byIndex3.byKey("driven_id").asInt(), (float) byIndex3.byKey("min1").asDouble(), (float) byIndex3.byKey("max1").asDouble(), (float) byIndex3.byKey("min2").asDouble(), (float) byIndex3.byKey("max2").asDouble()));
                            }
                            immutableList = builder2.build();
                        }
                        ImmutableMap immutableMap = null;
                        if (byIndex2.keyExists("skeleton")) {
                            EnumMap enumMap = new EnumMap(SLSkeletonBoneID.class);
                            LLSDNode byKey6 = byIndex2.byKey("skeleton");
                            for (int i5 = 0; i5 < byKey6.getCount(); i5++) {
                                LLSDNode byIndex4 = byKey6.byIndex(i5);
                                SLSkeletonBoneID sLSkeletonBoneID = SLSkeletonBoneID.bones.get(byIndex4.byKey("bone_id").asString());
                                if (sLSkeletonBoneID != null) {
                                    enumMap.put(sLSkeletonBoneID, new SLAvatarParams.SkeletonParamDefinition(vectorFromNode(byIndex4, "scale"), vectorFromNode(byIndex4, "offset")));
                                }
                            }
                            immutableMap = Maps.immutableEnumMap(enumMap);
                        }
                        builder.add((Object) new SLAvatarParams.AvatarParam(valueOf, (float) byIndex2.byKey("minValue").asDouble(), (float) byIndex2.byKey("maxValue").asDouble(), (float) byIndex2.byKey("defValue").asDouble(), byIndex2.byKey("morph").asBoolean(), sLAvatarParamColor, sLAvatarParamAlpha, immutableList, immutableMap));
                    }
                    SLAvatarParams.ParamSet paramSet = new SLAvatarParams.ParamSet(byIndex.byKey("setId").asInt(), byIndex.byKey("appearanceIndex").asInt(), SLVisualParamID.valueOf(byIndex.byKey("setName").asString()), builder.build());
                    map.put(Integer.valueOf(paramSet.id), paramSet);
                    if (paramSet.appearanceIndex != -1) {
                        paramSetArr[paramSet.appearanceIndex] = paramSet;
                    }
                }
                open.close();
            }
        } catch (IOException e) {
            Debug.Warning(e);
        } catch (LLSDException e2) {
            Debug.Warning(e2);
        }
    }

    @Nullable
    private static ImmutableVector vectorFromNode(LLSDNode lLSDNode, String str) throws LLSDException {
        if (!lLSDNode.keyExists(str)) {
            return null;
        }
        LLSDNode byKey = lLSDNode.byKey(str);
        return new ImmutableVector((float) byKey.byKey("x").asDouble(), (float) byKey.byKey("y").asDouble(), (float) byKey.byKey("z").asDouble());
    }
}
