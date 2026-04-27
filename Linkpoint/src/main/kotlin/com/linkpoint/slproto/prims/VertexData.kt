package com.linkpoint.slproto.prims

import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector4

class VertexData {
    public LLVector4 Normal
    public LLVector4 Position
    public LLVector2 TexCoord

    @JvmStatic
    fun LerpPlanarVertex(vertexData: VertexData, vertexData2: VertexData, vertexData3: VertexData, f: Float, f2: Float): VertexData {
        val sub: LLVector4 = LLVector4.sub(vertexData2.Position, vertexData.Position)
        sub.mul(f)
        val sub2: LLVector4 = LLVector4.sub(vertexData3.Position, vertexData.Position)
        sub2.mul(f2)
        sub2.add(sub)
        sub2.add(vertexData.Position)
        val vertexData4: VertexData = VertexData()
        vertexData4.Position = sub2
        if (vertexData.Normal != null) {
            vertexData4.Normal = LLVector4(vertexData.Normal)
        }
        val sub3: LLVector2 = LLVector2.sub(vertexData2.TexCoord, vertexData.TexCoord)
        sub3.mul(f)
        val sub4: LLVector2 = LLVector2.sub(vertexData3.TexCoord, vertexData.TexCoord)
        sub4.mul(f2)
        val lLVector2: LLVector2 = LLVector2(vertexData.TexCoord)
        lLVector2.add(sub3)
        lLVector2.add(sub4)
        vertexData4.TexCoord = lLVector2
        return vertexData4
    }
}
