// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import java.util.EnumMap;

public class MeshJointTranslations
{

    public final EnumMap jointTranslations = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/SLSkeletonBoneID);
    public float pelvisOffset;

    public MeshJointTranslations()
    {
        pelvisOffset = 0.0F;
    }
}
