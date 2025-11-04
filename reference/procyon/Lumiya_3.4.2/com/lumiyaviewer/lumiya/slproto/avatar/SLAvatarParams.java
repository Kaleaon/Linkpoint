// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.HashMap;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;

public class SLAvatarParams
{
    public static final int NUM_PARAMS = 218;
    @Nonnull
    public static final ImmutableMap<Integer, ParamSet> paramByIDs;
    @Nonnull
    public static final ParamSet[] paramDefs;
    
    static {
        paramDefs = new ParamSet[218];
        final HashMap hashMap = new HashMap();
        SLAvatarParamBuilder.buildParams(SLAvatarParams.paramDefs, hashMap);
        paramByIDs = ImmutableMap.copyOf((Map<?, ?>)hashMap);
    }
    
    public static class AvatarParam
    {
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
        
        AvatarParam(@Nullable final MeshIndex meshIndex, final float minValue, final float maxValue, final float defValue, final boolean morph, @Nullable final SLAvatarParamColor paramColor, @Nullable final SLAvatarParamAlpha paramAlpha, @Nullable final ImmutableList<DrivenParam> drivenParams, @Nullable final ImmutableMap<SLSkeletonBoneID, SkeletonParamDefinition> skeletonParams) {
            this.meshIndex = meshIndex;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.defValue = defValue;
            this.morph = morph;
            this.paramColor = paramColor;
            this.paramAlpha = paramAlpha;
            this.drivenParams = drivenParams;
            this.skeletonParams = skeletonParams;
        }
    }
    
    public static class DrivenParam
    {
        public final int drivenID;
        public final float max1;
        public final float max2;
        public final float min1;
        public final float min2;
        
        DrivenParam(final int drivenID, final float min1, final float max1, final float min2, final float max2) {
            this.drivenID = drivenID;
            this.min1 = min1;
            this.max1 = max1;
            this.min2 = min2;
            this.max2 = max2;
        }
    }
    
    public static class ParamSet
    {
        public final int appearanceIndex;
        public final int id;
        @Nonnull
        public final SLVisualParamID name;
        @Nonnull
        public final ImmutableList<AvatarParam> params;
        
        ParamSet(final int id, final int appearanceIndex, @Nonnull final SLVisualParamID name, @Nonnull final ImmutableList<AvatarParam> params) {
            this.id = id;
            this.appearanceIndex = appearanceIndex;
            this.name = name;
            this.params = params;
        }
    }
    
    public static class SkeletonParamDefinition
    {
        @Nullable
        public final ImmutableVector offset;
        @Nullable
        public final ImmutableVector scale;
        
        SkeletonParamDefinition(@Nullable final ImmutableVector scale, @Nullable final ImmutableVector offset) {
            this.scale = scale;
            this.offset = offset;
        }
    }
    
    public static class SkeletonParamValue
    {
        @Nonnull
        public final LLVector3 offset;
        @Nonnull
        public final LLVector3 scale;
        
        public SkeletonParamValue(@Nonnull final LLVector3 scale, @Nonnull final LLVector3 offset) {
            this.scale = scale;
            this.offset = offset;
        }
    }
}
