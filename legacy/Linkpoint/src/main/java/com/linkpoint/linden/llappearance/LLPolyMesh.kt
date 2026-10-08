package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llmath.Vector3

class LLPolyMesh(val meshName: String) {

    var vertices: FloatArray = FloatArray(0)
        private set
    var normals: FloatArray = FloatArray(0)
        private set
    var texCoords: FloatArray = FloatArray(0)
        private set
    var indices: ShortArray = ShortArray(0)
        private set

    val numVertices: Int get() = vertices.size / 3
    val numTriangles: Int get() = indices.size / 3

    private val morphTargets: MutableMap<String, LLPolyMorphTarget> = mutableMapOf()
    private var baseVertices: FloatArray = FloatArray(0)
    private var baseNormals: FloatArray = FloatArray(0)

    fun buildFromArrays(
        verts: FloatArray,
        norms: FloatArray,
        uvs: FloatArray,
        idx: ShortArray,
    ): Boolean {
        require(verts.size % 3 == 0) { "Vertex array must be a multiple of 3" }
        require(norms.size == verts.size) { "Normal array size must match vertex array" }
        require(uvs.size == (verts.size / 3) * 2) { "UV array size mismatch" }
        vertices = verts.copyOf()
        normals = norms.copyOf()
        texCoords = uvs.copyOf()
        indices = idx.copyOf()
        baseVertices = verts.copyOf()
        baseNormals = norms.copyOf()
        return true
    }

    fun buildFromFile(path: String): Boolean {
        // Stub file loader — real implementation would parse .llm binary mesh format
        val numVerts = 4
        vertices = floatArrayOf(
            -0.5f, -0.5f, 0f,
             0.5f, -0.5f, 0f,
             0.5f,  0.5f, 0f,
            -0.5f,  0.5f, 0f,
        )
        normals = floatArrayOf(
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
        )
        texCoords = floatArrayOf(0f, 0f,  1f, 0f,  1f, 1f,  0f, 1f)
        indices = shortArrayOf(0, 1, 2,  0, 2, 3)
        baseVertices = vertices.copyOf()
        baseNormals = normals.copyOf()
        return true
    }

    fun addMorphTarget(target: LLPolyMorphTarget) {
        morphTargets[target.name] = target
    }

    fun getMorphTarget(name: String): LLPolyMorphTarget? = morphTargets[name]

    fun applyMorph(name: String, weight: Float): Boolean {
        val target = morphTargets[name] ?: return false
        return target.apply(vertices, normals, weight)
    }

    fun resetToBase() {
        vertices = baseVertices.copyOf()
        normals = baseNormals.copyOf()
        morphTargets.values.forEach { it.reset(vertices, normals) }
    }

    fun getMorphTargetNames(): Set<String> = morphTargets.keys.toSet()

    fun getVertex(index: Int): Vector3 {
        val i = index * 3
        return Vector3(vertices[i], vertices[i + 1], vertices[i + 2])
    }

    fun getNormal(index: Int): Vector3 {
        val i = index * 3
        return Vector3(normals[i], normals[i + 1], normals[i + 2])
    }

    override fun toString(): String =
        "LLPolyMesh($meshName, verts=$numVertices, tris=$numTriangles, morphs=${morphTargets.size})"
}
