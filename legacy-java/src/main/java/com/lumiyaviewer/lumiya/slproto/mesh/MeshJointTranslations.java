package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import java.util.EnumMap;

/* loaded from: classes.dex */
public class MeshJointTranslations {
    public float pelvisOffset = 0.0f;
    public final EnumMap<SLSkeletonBoneID, float[]> jointTranslations = new EnumMap<>(SLSkeletonBoneID.class);
}
