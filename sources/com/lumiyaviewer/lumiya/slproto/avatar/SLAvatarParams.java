// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.HashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLAvatarParamBuilder, MeshIndex, SLAvatarParamAlpha, SLAvatarParamColor, 
//            SLVisualParamID

public class SLAvatarParams
{
    public static class AvatarParam
    {

        public final float defValue;
        public final ImmutableList drivenParams;
        public final float maxValue;
        public final MeshIndex meshIndex;
        public final float minValue;
        public final boolean morph;
        public final SLAvatarParamAlpha paramAlpha;
        public final SLAvatarParamColor paramColor;
        public final ImmutableMap skeletonParams;

        AvatarParam(MeshIndex meshindex, float f, float f1, float f2, boolean flag, SLAvatarParamColor slavatarparamcolor, SLAvatarParamAlpha slavatarparamalpha, 
                ImmutableList immutablelist, ImmutableMap immutablemap)
        {
            meshIndex = meshindex;
            minValue = f;
            maxValue = f1;
            defValue = f2;
            morph = flag;
            paramColor = slavatarparamcolor;
            paramAlpha = slavatarparamalpha;
            drivenParams = immutablelist;
            skeletonParams = immutablemap;
        }
    }

    public static class DrivenParam
    {

        public final int drivenID;
        public final float max1;
        public final float max2;
        public final float min1;
        public final float min2;

        DrivenParam(int i, float f, float f1, float f2, float f3)
        {
            drivenID = i;
            min1 = f;
            max1 = f1;
            min2 = f2;
            max2 = f3;
        }
    }

    public static class ParamSet
    {

        public final int appearanceIndex;
        public final int id;
        public final SLVisualParamID name;
        public final ImmutableList params;

        ParamSet(int i, int j, SLVisualParamID slvisualparamid, ImmutableList immutablelist)
        {
            id = i;
            appearanceIndex = j;
            name = slvisualparamid;
            params = immutablelist;
        }
    }

    public static class SkeletonParamDefinition
    {

        public final ImmutableVector offset;
        public final ImmutableVector scale;

        SkeletonParamDefinition(ImmutableVector immutablevector, ImmutableVector immutablevector1)
        {
            scale = immutablevector;
            offset = immutablevector1;
        }
    }

    public static class SkeletonParamValue
    {

        public final LLVector3 offset;
        public final LLVector3 scale;

        public SkeletonParamValue(LLVector3 llvector3, LLVector3 llvector3_1)
        {
            scale = llvector3;
            offset = llvector3_1;
        }
    }


    public static final int NUM_PARAMS = 218;
    public static final ImmutableMap paramByIDs;
    public static final ParamSet paramDefs[];

    public SLAvatarParams()
    {
    }

    static 
    {
        paramDefs = new ParamSet[218];
        HashMap hashmap = new HashMap();
        SLAvatarParamBuilder.buildParams(paramDefs, hashmap);
        paramByIDs = ImmutableMap.copyOf(hashmap);
    }
}
