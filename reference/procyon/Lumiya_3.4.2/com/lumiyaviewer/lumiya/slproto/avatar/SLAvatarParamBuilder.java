// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import java.io.InputStream;
import android.content.res.AssetManager;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.Debug;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.EnumMap;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.util.Map;

class SLAvatarParamBuilder
{
    static void buildParams(final SLAvatarParams.ParamSet[] array, final Map<Integer, SLAvatarParams.ParamSet> map) {
        try {
            final AssetManager assetManager = LumiyaApp.getAssetManager();
            if (assetManager != null) {
                final InputStream open = assetManager.open("character/avatar_params.xml", 3);
                final LLSDNode xml = LLSDNode.parseXML(open, "UTF-8");
                for (int count = xml.getCount(), i = 0; i < count; ++i) {
                    final LLSDNode byIndex = xml.byIndex(i);
                    final LLSDNode byKey = byIndex.byKey("params");
                    final int count2 = byKey.getCount();
                    final ImmutableList.Builder<SLAvatarParams.AvatarParam> builder = ImmutableList.builder();
                    for (int j = 0; j < count2; ++j) {
                        final LLSDNode byIndex2 = byKey.byIndex(j);
                        SLAvatarParamColor slAvatarParamColor = null;
                        SLAvatarParamAlpha slAvatarParamAlpha = null;
                        if (byIndex2.keyExists("paramColor")) {
                            final LLSDNode byKey2 = byIndex2.byKey("paramColor");
                            final LLSDNode byKey3 = byKey2.byKey("values");
                            final int[] array2 = new int[byKey3.getCount()];
                            for (int k = 0; k < array2.length; ++k) {
                                array2[k] = byKey3.byIndex(k).asInt();
                            }
                            slAvatarParamColor = new SLAvatarParamColor(SLAvatarParamColor.ColorOperation.valueOf(byKey2.byKey("opcode").asString()), array2);
                        }
                        if (byIndex2.keyExists("paramAlpha")) {
                            final LLSDNode byKey4 = byIndex2.byKey("paramAlpha");
                            final float n = (float)byKey4.byKey("domain").asDouble();
                            String string;
                            if (byKey4.keyExists("tgaFile")) {
                                string = byKey4.byKey("tgaFile").asString();
                            }
                            else {
                                string = null;
                            }
                            slAvatarParamAlpha = new SLAvatarParamAlpha(n, string, byKey4.byKey("skipIfZero").asBoolean(), byKey4.byKey("multiplyBlend").asBoolean());
                        }
                        MeshIndex value;
                        if (byIndex2.keyExists("meshIndex")) {
                            value = MeshIndex.valueOf(byIndex2.byKey("meshIndex").asString());
                        }
                        else {
                            value = null;
                        }
                        ImmutableList<SLAvatarParams.DrivenParam> build = null;
                        if (byIndex2.keyExists("driven")) {
                            final ImmutableList.Builder<SLAvatarParams.DrivenParam> builder2 = ImmutableList.builder();
                            final LLSDNode byKey5 = byIndex2.byKey("driven");
                            for (int l = 0; l < byKey5.getCount(); ++l) {
                                final LLSDNode byIndex3 = byKey5.byIndex(l);
                                builder2.add(new SLAvatarParams.DrivenParam(byIndex3.byKey("driven_id").asInt(), (float)byIndex3.byKey("min1").asDouble(), (float)byIndex3.byKey("max1").asDouble(), (float)byIndex3.byKey("min2").asDouble(), (float)byIndex3.byKey("max2").asDouble()));
                            }
                            build = builder2.build();
                        }
                        ImmutableMap<SLSkeletonBoneID, Object> immutableEnumMap = null;
                        if (byIndex2.keyExists("skeleton")) {
                            final EnumMap enumMap = new EnumMap(SLSkeletonBoneID.class);
                            final LLSDNode byKey6 = byIndex2.byKey("skeleton");
                            for (int n2 = 0; n2 < byKey6.getCount(); ++n2) {
                                final LLSDNode byIndex4 = byKey6.byIndex(n2);
                                final SLSkeletonBoneID slSkeletonBoneID = SLSkeletonBoneID.bones.get(byIndex4.byKey("bone_id").asString());
                                if (slSkeletonBoneID != null) {
                                    enumMap.put(slSkeletonBoneID, new SLAvatarParams.SkeletonParamDefinition(vectorFromNode(byIndex4, "scale"), vectorFromNode(byIndex4, "offset")));
                                }
                            }
                            immutableEnumMap = Maps.immutableEnumMap((Map<SLSkeletonBoneID, ?>)enumMap);
                        }
                        builder.add(new SLAvatarParams.AvatarParam(value, (float)byIndex2.byKey("minValue").asDouble(), (float)byIndex2.byKey("maxValue").asDouble(), (float)byIndex2.byKey("defValue").asDouble(), byIndex2.byKey("morph").asBoolean(), slAvatarParamColor, slAvatarParamAlpha, build, (ImmutableMap<SLSkeletonBoneID, SLAvatarParams.SkeletonParamDefinition>)immutableEnumMap));
                    }
                    final SLAvatarParams.ParamSet set = new SLAvatarParams.ParamSet(byIndex.byKey("setId").asInt(), byIndex.byKey("appearanceIndex").asInt(), SLVisualParamID.valueOf(byIndex.byKey("setName").asString()), builder.build());
                    map.put(set.id, set);
                    if (set.appearanceIndex != -1) {
                        array[set.appearanceIndex] = set;
                    }
                }
                open.close();
            }
        }
        catch (final LLSDException ex) {
            Debug.Warning(ex);
        }
        catch (final IOException ex2) {
            Debug.Warning(ex2);
        }
    }
    
    @Nullable
    private static ImmutableVector vectorFromNode(LLSDNode byKey, final String s) throws LLSDException {
        if (byKey.keyExists(s)) {
            byKey = byKey.byKey(s);
            return new ImmutableVector((float)byKey.byKey("x").asDouble(), (float)byKey.byKey("y").asDouble(), (float)byKey.byKey("z").asDouble());
        }
        return null;
    }
}
