// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.types;

public class VertexArray
{
    private Vector3Array normals;
    private Vector2Array texCoords;
    private VectorArray vertexAndNormalsData;
    private Vector3Array vertices;
    
    public VertexArray(final int n) {
        this.vertexAndNormalsData = new VectorArray(6, n);
        this.vertices = new Vector3Array(this.vertexAndNormalsData, 0);
        this.normals = new Vector3Array(this.vertexAndNormalsData, 3);
        this.texCoords = new Vector2Array(n);
    }
    
    public void LerpPlanarVertex(final int n, final VertexArray vertexArray, final int n2, final VertexArray vertexArray2, final int n3, final VertexArray vertexArray3, final int n4, final float n5, final float n6, final LLVector3 llVector3, final LLVector3 llVector4, final LLVector2 llVector5, final LLVector2 llVector6) {
        vertexArray2.vertices.getSub(n3, vertexArray.vertices, n2, llVector3);
        llVector3.mul(n5);
        vertexArray3.vertices.getSub(n4, vertexArray.vertices, n2, llVector4);
        llVector4.mul(n6);
        llVector4.add(llVector3);
        vertexArray.vertices.addToVector(n2, llVector4);
        this.vertices.set(n, llVector4);
        this.normals.set(n, vertexArray.normals, n2);
        vertexArray2.texCoords.getSub(n3, vertexArray.texCoords, n2, llVector5);
        llVector5.mul(n5);
        vertexArray3.texCoords.getSub(n4, vertexArray.texCoords, n2, llVector6);
        llVector6.mul(n6);
        llVector6.add(llVector5);
        vertexArray.texCoords.addToVector(n2, llVector6);
        this.texCoords.set(n, llVector6.x, llVector6.y);
    }
    
    public float[] getData() {
        return this.vertexAndNormalsData.getData();
    }
    
    public int getLength() {
        return this.vertexAndNormalsData.getLength();
    }
    
    public Vector3Array getNormals() {
        return this.normals;
    }
    
    public Vector2Array getTexCoords() {
        return this.texCoords;
    }
    
    public float[] getTexCoordsData() {
        return this.texCoords.getData();
    }
    
    public Vector3Array getVertices() {
        return this.vertices;
    }
}
