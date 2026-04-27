// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            VectorArray, Vector3Array, Vector2Array, LLVector3, 
//            LLVector2

public class VertexArray
{

    private Vector3Array normals;
    private Vector2Array texCoords;
    private VectorArray vertexAndNormalsData;
    private Vector3Array vertices;

    public VertexArray(int i)
    {
        vertexAndNormalsData = new VectorArray(6, i);
        vertices = new Vector3Array(vertexAndNormalsData, 0);
        normals = new Vector3Array(vertexAndNormalsData, 3);
        texCoords = new Vector2Array(i);
    }

    public void LerpPlanarVertex(int i, VertexArray vertexarray, int j, VertexArray vertexarray1, int k, VertexArray vertexarray2, int l, 
            float f, float f1, LLVector3 llvector3, LLVector3 llvector3_1, LLVector2 llvector2, LLVector2 llvector2_1)
    {
        vertexarray1.vertices.getSub(k, vertexarray.vertices, j, llvector3);
        llvector3.mul(f);
        vertexarray2.vertices.getSub(l, vertexarray.vertices, j, llvector3_1);
        llvector3_1.mul(f1);
        llvector3_1.add(llvector3);
        vertexarray.vertices.addToVector(j, llvector3_1);
        vertices.set(i, llvector3_1);
        normals.set(i, vertexarray.normals, j);
        vertexarray1.texCoords.getSub(k, vertexarray.texCoords, j, llvector2);
        llvector2.mul(f);
        vertexarray2.texCoords.getSub(l, vertexarray.texCoords, j, llvector2_1);
        llvector2_1.mul(f1);
        llvector2_1.add(llvector2);
        vertexarray.texCoords.addToVector(j, llvector2_1);
        texCoords.set(i, llvector2_1.x, llvector2_1.y);
    }

    public float[] getData()
    {
        return vertexAndNormalsData.getData();
    }

    public int getLength()
    {
        return vertexAndNormalsData.getLength();
    }

    public Vector3Array getNormals()
    {
        return normals;
    }

    public Vector2Array getTexCoords()
    {
        return texCoords;
    }

    public float[] getTexCoordsData()
    {
        return texCoords.getData();
    }

    public Vector3Array getVertices()
    {
        return vertices;
    }
}
