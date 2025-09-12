// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLPolyMesh

public class SLMeshData
{

    protected DirectByteBuffer indexBuffer;
    protected int numFaces;
    protected int numVertices;
    protected LLVector3 position;
    protected SLPolyMesh referenceData;
    protected LLQuaternion rotation;
    protected LLVector3 scale;
    protected DirectByteBuffer texCoordsBuffer;
    protected DirectByteBuffer vertexBuffer;

    public SLMeshData()
    {
    }

    public SLMeshData(SLPolyMesh slpolymesh)
    {
        referenceData = slpolymesh;
        position = new LLVector3(slpolymesh.position);
        scale = new LLVector3(slpolymesh.scale);
        rotation = new LLQuaternion(slpolymesh.rotation);
        numVertices = slpolymesh.numVertices;
        vertexBuffer = new DirectByteBuffer(slpolymesh.vertexBuffer);
        texCoordsBuffer = new DirectByteBuffer(slpolymesh.texCoordsBuffer);
        numFaces = slpolymesh.numFaces;
        indexBuffer = new DirectByteBuffer(slpolymesh.indexBuffer);
    }

    public void initFromReference()
    {
        vertexBuffer.copyFrom(0, referenceData.vertexBuffer, 0, referenceData.vertexBuffer.asByteBuffer().capacity());
        texCoordsBuffer.copyFrom(0, referenceData.texCoordsBuffer, 0, referenceData.texCoordsBuffer.asByteBuffer().capacity());
        indexBuffer.copyFrom(0, referenceData.indexBuffer, 0, referenceData.indexBuffer.asByteBuffer().capacity());
    }
}
