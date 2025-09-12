// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBone;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarSkeleton

private static class <init>
{

    final SLSkeletonBone bone;
    public final float matrix[];
    public final SLAttachmentPoint point;

    private (SLSkeletonBone slskeletonbone, SLAttachmentPoint slattachmentpoint)
    {
        matrix = new float[16];
        bone = slskeletonbone;
        point = slattachmentpoint;
    }

    point(SLSkeletonBone slskeletonbone, SLAttachmentPoint slattachmentpoint, point point1)
    {
        this(slskeletonbone, slattachmentpoint);
    }
}
