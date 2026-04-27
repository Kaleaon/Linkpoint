// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector4;

public class VertexData
{
    public LLVector4 Normal;
    public LLVector4 Position;
    public LLVector2 TexCoord;
    
    public static VertexData LerpPlanarVertex(final VertexData vertexData, final VertexData vertexData2, final VertexData vertexData3, final float n, final float n2) {
        final LLVector4 sub = LLVector4.sub(vertexData2.Position, vertexData.Position);
        sub.mul(n);
        final LLVector4 sub2 = LLVector4.sub(vertexData3.Position, vertexData.Position);
        sub2.mul(n2);
        sub2.add(sub);
        sub2.add(vertexData.Position);
        final VertexData vertexData4 = new VertexData();
        vertexData4.Position = sub2;
        if (vertexData.Normal != null) {
            vertexData4.Normal = new LLVector4(vertexData.Normal);
        }
        final LLVector2 sub3 = LLVector2.sub(vertexData2.TexCoord, vertexData.TexCoord);
        sub3.mul(n);
        final LLVector2 sub4 = LLVector2.sub(vertexData3.TexCoord, vertexData.TexCoord);
        sub4.mul(n2);
        final LLVector2 texCoord = new LLVector2(vertexData.TexCoord);
        texCoord.add(sub3);
        texCoord.add(sub4);
        vertexData4.TexCoord = texCoord;
        return vertexData4;
    }
}
