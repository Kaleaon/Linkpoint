// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class VertexData
{

    public LLVector4 Normal;
    public LLVector4 Position;
    public LLVector2 TexCoord;

    public VertexData()
    {
    }

    public static VertexData LerpPlanarVertex(VertexData vertexdata, VertexData vertexdata1, VertexData vertexdata2, float f, float f1)
    {
        Object obj = LLVector4.sub(vertexdata1.Position, vertexdata.Position);
        ((LLVector4) (obj)).mul(f);
        LLVector4 llvector4 = LLVector4.sub(vertexdata2.Position, vertexdata.Position);
        llvector4.mul(f1);
        llvector4.add(((LLVector4) (obj)));
        llvector4.add(vertexdata.Position);
        obj = new VertexData();
        obj.Position = llvector4;
        if (vertexdata.Normal != null)
        {
            obj.Normal = new LLVector4(vertexdata.Normal);
        }
        vertexdata1 = LLVector2.sub(vertexdata1.TexCoord, vertexdata.TexCoord);
        vertexdata1.mul(f);
        vertexdata2 = LLVector2.sub(vertexdata2.TexCoord, vertexdata.TexCoord);
        vertexdata2.mul(f1);
        vertexdata = new LLVector2(vertexdata.TexCoord);
        vertexdata.add(vertexdata1);
        vertexdata.add(vertexdata2);
        obj.TexCoord = vertexdata;
        return ((VertexData) (obj));
    }
}
