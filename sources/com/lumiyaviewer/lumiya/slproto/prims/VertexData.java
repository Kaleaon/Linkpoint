package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class VertexData {
    public LLVector4 Normal;
    public LLVector4 Position;
    public LLVector2 TexCoord;

    public static VertexData LerpPlanarVertex(VertexData vertexData, VertexData vertexData2, VertexData vertexData3, float f, float f2) {
        LLVector4 sub = LLVector4.sub(vertexData2.Position, vertexData.Position);
        sub.mul(f);
        LLVector4 sub2 = LLVector4.sub(vertexData3.Position, vertexData.Position);
        sub2.mul(f2);
        sub2.add(sub);
        sub2.add(vertexData.Position);
        VertexData vertexData4 = new VertexData();
        vertexData4.Position = sub2;
        if (vertexData.Normal != null) {
            vertexData4.Normal = new LLVector4(vertexData.Normal);
        }
        LLVector2 sub3 = LLVector2.sub(vertexData2.TexCoord, vertexData.TexCoord);
        sub3.mul(f);
        LLVector2 sub4 = LLVector2.sub(vertexData3.TexCoord, vertexData.TexCoord);
        sub4.mul(f2);
        LLVector2 lLVector2 = new LLVector2(vertexData.TexCoord);
        lLVector2.add(sub3);
        lLVector2.add(sub4);
        vertexData4.TexCoord = lLVector2;
        return vertexData4;
    }
}
