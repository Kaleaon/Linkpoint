// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.mesh;

import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class MeshWeightsBuffer
{

    public final DirectByteBuffer jointIndexBuffer;
    public final DirectByteBuffer weightsBuffer;

    public MeshWeightsBuffer(int i)
    {
        jointIndexBuffer = new DirectByteBuffer(i * 4);
        weightsBuffer = new DirectByteBuffer(i * 4 * 4);
    }
}
