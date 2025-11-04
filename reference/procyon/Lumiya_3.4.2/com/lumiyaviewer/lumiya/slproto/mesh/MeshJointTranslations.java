// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import java.util.EnumMap;

public class MeshJointTranslations
{
    public final EnumMap<SLSkeletonBoneID, float[]> jointTranslations;
    public float pelvisOffset;
    
    public MeshJointTranslations() {
        this.pelvisOffset = 0.0f;
        this.jointTranslations = new EnumMap<SLSkeletonBoneID, float[]>(SLSkeletonBoneID.class);
    }
}
