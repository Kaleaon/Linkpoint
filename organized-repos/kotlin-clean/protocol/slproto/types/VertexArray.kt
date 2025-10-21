package com.linkpoint.slproto.types

class VertexArray {
    private Vector3Array normals = Vector3Array(this.vertexAndNormalsData, 3)
    private Vector2Array texCoords
    private VectorArray vertexAndNormalsData
    private Vector3Array vertices = Vector3Array(this.vertexAndNormalsData, 0)

    public VertexArray(Int i) {
        this.vertexAndNormalsData = VectorArray(6, i)
        this.texCoords = Vector2Array(i)
    }

    fun LerpPlanarVertex(Int i, VertexArray vertexArray, Int i2, VertexArray vertexArray2, Int i3, VertexArray vertexArray3, Int i4, Float f, Float f2, LLVector3 lLVector3, LLVector3 lLVector32, LLVector2 lLVector2, LLVector2 lLVector22) {
        vertexArray2.vertices.getSub(i3, vertexArray.vertices, i2, lLVector3)
        lLVector3.mul(f)
        vertexArray3.vertices.getSub(i4, vertexArray.vertices, i2, lLVector32)
        lLVector32.mul(f2)
        lLVector32.add(lLVector3)
        vertexArray.vertices.addToVector(i2, lLVector32)
        this.vertices.set(i, lLVector32)
        this.normals.set(i, vertexArray.normals, i2)
        vertexArray2.texCoords.getSub(i3, vertexArray.texCoords, i2, lLVector2)
        lLVector2.mul(f)
        vertexArray3.texCoords.getSub(i4, vertexArray.texCoords, i2, lLVector22)
        lLVector22.mul(f2)
        lLVector22.add(lLVector2)
        vertexArray.texCoords.addToVector(i2, lLVector22)
        this.texCoords.set(i, lLVector22.x, lLVector22.y)
    }

    public Float[] getData() {
        return this.vertexAndNormalsData.getData()
    }

    public Int getLength() {
        return this.vertexAndNormalsData.getLength()
    }

    public Vector3Array getNormals() {
        return this.normals
    }

    public Vector2Array getTexCoords() {
        return this.texCoords
    }

    public Float[] getTexCoordsData() {
        return this.texCoords.getData()
    }

    public Vector3Array getVertices() {
        return this.vertices
    }
}
