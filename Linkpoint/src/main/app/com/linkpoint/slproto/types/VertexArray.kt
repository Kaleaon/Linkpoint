package com.linkpoint.slproto.types

class VertexArray(length: Int) {
    private val vertexAndNormalsData: VectorArray = VectorArray(6, length)
    private val vertices: Vector3Array = Vector3Array(vertexAndNormalsData, 0)
    private val normals: Vector3Array = Vector3Array(vertexAndNormalsData, 3)
    private val texCoords: Vector2Array = Vector2Array(length)

    fun LerpPlanarVertex(
        index: Int,
        baseVertex: VertexArray,
        baseIndex: Int,
        vertex2: VertexArray,
        index2: Int,
        vertex3: VertexArray,
        index3: Int,
        weight2: Float,
        weight3: Float,
        tempVec3_1: LLVector3,
        tempVec3_2: LLVector3,
        tempVec2_1: LLVector2,
        tempVec2_2: LLVector2,
    ) {
        // Calculate vertex position
        vertex2.vertices.getSub(index2, baseVertex.vertices, baseIndex, tempVec3_1)
        tempVec3_1.mul(weight2)
        vertex3.vertices.getSub(index3, baseVertex.vertices, baseIndex, tempVec3_2)
        tempVec3_2.mul(weight3)
        tempVec3_2.add(tempVec3_1)
        baseVertex.vertices.addToVector(baseIndex, tempVec3_2)
        vertices.set(index, tempVec3_2)

        // Set normals
        normals.set(index, baseVertex.normals, baseIndex)

        // Calculate texture coordinates
        vertex2.texCoords.getSub(index2, baseVertex.texCoords, baseIndex, tempVec2_1)
        tempVec2_1.mul(weight2)
        vertex3.texCoords.getSub(index3, baseVertex.texCoords, baseIndex, tempVec2_2)
        tempVec2_2.mul(weight3)
        tempVec2_2.add(tempVec2_1)
        baseVertex.texCoords.addToVector(baseIndex, tempVec2_2)
        texCoords.set(index, tempVec2_2.x, tempVec2_2.y)
    }

    fun getData(): FloatArray {
        return vertexAndNormalsData.getData()
    }

    fun getLength(): Int {
        return vertexAndNormalsData.getLength()
    }

    fun getNormals(): Vector3Array {
        return normals
    }

    fun getTexCoords(): Vector2Array {
        return texCoords
    }

    fun getTexCoordsData(): FloatArray {
        return texCoords.getData()
    }

    fun getVertices(): Vector3Array {
        return vertices
    }
}
