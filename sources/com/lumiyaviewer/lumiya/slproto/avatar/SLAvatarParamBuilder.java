// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLAvatarParamColor, SLAvatarParamAlpha, MeshIndex, SLSkeletonBoneID, 
//            SLVisualParamID

class SLAvatarParamBuilder
{

    SLAvatarParamBuilder()
    {
    }

    static void buildParams(SLAvatarParams.ParamSet aparamset[], Map map)
    {
        float f;
        Object obj;
        Object obj1;
        Object obj2;
        int ai[];
        Object obj3;
        Object obj4;
        InputStream inputstream;
        LLSDNode llsdnode;
        LLSDNode llsdnode1;
        LLSDNode llsdnode2;
        com.google.common.collect.ImmutableList.Builder builder;
        LLSDNode llsdnode3;
        LLSDNode llsdnode5;
        LLSDNode llsdnode6;
        SLSkeletonBoneID slskeletonboneid;
        int i;
        int j;
        int k;
        int l;
        int i1;
        try
        {
            obj = LumiyaApp.getAssetManager();
        }
        // Misplaced declaration of an exception variable
        catch (SLAvatarParams.ParamSet aparamset[])
        {
            Debug.Warning(aparamset);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SLAvatarParams.ParamSet aparamset[])
        {
            Debug.Warning(aparamset);
            return;
        }
        if (obj == null) goto _L2; else goto _L1
_L1:
        inputstream = ((AssetManager) (obj)).open("character/avatar_params.xml", 3);
        llsdnode = LLSDNode.parseXML(inputstream, "UTF-8");
        l = llsdnode.getCount();
        i = 0;
_L29:
        if (i >= l) goto _L4; else goto _L3
_L3:
        llsdnode1 = llsdnode.byIndex(i);
        llsdnode2 = llsdnode1.byKey("params");
        i1 = llsdnode2.getCount();
        builder = ImmutableList.builder();
        j = 0;
_L23:
        if (j >= i1) goto _L6; else goto _L5
_L5:
        llsdnode3 = llsdnode2.byIndex(j);
        obj = null;
        obj1 = null;
        if (!llsdnode3.keyExists("paramColor"))
        {
            break MISSING_BLOCK_LABEL_189;
        }
        obj = llsdnode3.byKey("paramColor");
        obj2 = ((LLSDNode) (obj)).byKey("values");
        ai = new int[((LLSDNode) (obj2)).getCount()];
        k = 0;
_L8:
        if (k >= ai.length)
        {
            break; /* Loop/switch isn't completed */
        }
        ai[k] = ((LLSDNode) (obj2)).byIndex(k).asInt();
        k++;
        if (true) goto _L8; else goto _L7
_L7:
        obj = new SLAvatarParamColor(SLAvatarParamColor.ColorOperation.valueOf(((LLSDNode) (obj)).byKey("opcode").asString()), ai);
        if (!llsdnode3.keyExists("paramAlpha")) goto _L10; else goto _L9
_L9:
        obj2 = llsdnode3.byKey("paramAlpha");
        f = (float)((LLSDNode) (obj2)).byKey("domain").asDouble();
        if (!((LLSDNode) (obj2)).keyExists("tgaFile")) goto _L12; else goto _L11
_L11:
        obj1 = ((LLSDNode) (obj2)).byKey("tgaFile").asString();
_L25:
        obj1 = new SLAvatarParamAlpha(f, ((String) (obj1)), ((LLSDNode) (obj2)).byKey("skipIfZero").asBoolean(), ((LLSDNode) (obj2)).byKey("multiplyBlend").asBoolean());
_L10:
        if (!llsdnode3.keyExists("meshIndex")) goto _L14; else goto _L13
_L13:
        obj2 = MeshIndex.valueOf(llsdnode3.byKey("meshIndex").asString());
_L26:
        obj3 = null;
        if (!llsdnode3.keyExists("driven"))
        {
            break MISSING_BLOCK_LABEL_431;
        }
        obj3 = ImmutableList.builder();
        obj4 = llsdnode3.byKey("driven");
        k = 0;
_L16:
        if (k >= ((LLSDNode) (obj4)).getCount())
        {
            break; /* Loop/switch isn't completed */
        }
        LLSDNode llsdnode4 = ((LLSDNode) (obj4)).byIndex(k);
        ((com.google.common.collect.ImmutableList.Builder) (obj3)).add(new SLAvatarParams.DrivenParam(llsdnode4.byKey("driven_id").asInt(), (float)llsdnode4.byKey("min1").asDouble(), (float)llsdnode4.byKey("max1").asDouble(), (float)llsdnode4.byKey("min2").asDouble(), (float)llsdnode4.byKey("max2").asDouble()));
        k++;
        if (true) goto _L16; else goto _L15
_L15:
        obj3 = ((com.google.common.collect.ImmutableList.Builder) (obj3)).build();
        obj4 = null;
        if (!llsdnode3.keyExists("skeleton")) goto _L18; else goto _L17
_L17:
        obj4 = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID);
        llsdnode5 = llsdnode3.byKey("skeleton");
        k = 0;
_L27:
        if (k >= llsdnode5.getCount()) goto _L20; else goto _L19
_L19:
        llsdnode6 = llsdnode5.byIndex(k);
        slskeletonboneid = (SLSkeletonBoneID)SLSkeletonBoneID.bones.get(llsdnode6.byKey("bone_id").asString());
        if (slskeletonboneid == null) goto _L22; else goto _L21
_L21:
        ((Map) (obj4)).put(slskeletonboneid, new SLAvatarParams.SkeletonParamDefinition(vectorFromNode(llsdnode6, "scale"), vectorFromNode(llsdnode6, "offset")));
          goto _L22
_L20:
        obj4 = Maps.immutableEnumMap(((Map) (obj4)));
_L18:
        builder.add(new SLAvatarParams.AvatarParam(((MeshIndex) (obj2)), (float)llsdnode3.byKey("minValue").asDouble(), (float)llsdnode3.byKey("maxValue").asDouble(), (float)llsdnode3.byKey("defValue").asDouble(), llsdnode3.byKey("morph").asBoolean(), ((SLAvatarParamColor) (obj)), ((SLAvatarParamAlpha) (obj1)), ((ImmutableList) (obj3)), ((ImmutableMap) (obj4))));
        j++;
          goto _L23
_L6:
        SLAvatarParams.ParamSet paramset = new SLAvatarParams.ParamSet(llsdnode1.byKey("setId").asInt(), llsdnode1.byKey("appearanceIndex").asInt(), SLVisualParamID.valueOf(llsdnode1.byKey("setName").asString()), builder.build());
        map.put(Integer.valueOf(paramset.id), paramset);
        if (paramset.appearanceIndex != -1)
        {
            aparamset[paramset.appearanceIndex] = paramset;
        }
          goto _L24
_L4:
        inputstream.close();
_L2:
        return;
_L12:
        obj1 = null;
          goto _L25
_L14:
        obj2 = null;
          goto _L26
_L22:
        k++;
          goto _L27
_L24:
        i++;
        if (true) goto _L29; else goto _L28
_L28:
    }

    private static ImmutableVector vectorFromNode(LLSDNode llsdnode, String s)
        throws LLSDException
    {
        if (llsdnode.keyExists(s))
        {
            llsdnode = llsdnode.byKey(s);
            return new ImmutableVector((float)llsdnode.byKey("x").asDouble(), (float)llsdnode.byKey("y").asDouble(), (float)llsdnode.byKey("z").asDouble());
        } else
        {
            return null;
        }
    }
}
