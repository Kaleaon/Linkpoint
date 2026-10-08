package com.linkpoint.linden.llrender

import com.linkpoint.linden.llmath.Matrix4
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.Vector4

class LLGLSLShader(val name: String) {

    var programId: Int = 0
        private set
    var vertShaderId: Int = 0
        private set
    var fragShaderId: Int = 0
        private set
    var isLinked: Boolean = false
        private set
    var isBound: Boolean = false
        private set

    private val attribMap: MutableMap<String, Int> = mutableMapOf()
    private val uniformMap: MutableMap<String, Any> = mutableMapOf()
    private val uniformLocations: MutableMap<String, Int> = mutableMapOf()
    private var nextAttribIndex: Int = 0

    val errors: MutableList<String> = mutableListOf()

    fun compile(vertSrc: String, fragSrc: String): Boolean {
        errors.clear()
        if (vertSrc.isBlank() || fragSrc.isBlank()) {
            errors.add("Shader source must not be blank")
            return false
        }
        vertShaderId = vertSrc.hashCode()
        fragShaderId = fragSrc.hashCode()
        return link()
    }

    fun link(): Boolean {
        if (vertShaderId == 0 || fragShaderId == 0) {
            errors.add("Cannot link: missing shader stages")
            return false
        }
        programId = vertShaderId xor fragShaderId
        isLinked = true
        return true
    }

    fun bind() {
        if (!isLinked) return
        isBound = true
    }

    fun unbind() {
        isBound = false
    }

    fun attribIndex(name: String): Int =
        attribMap.getOrPut(name) { nextAttribIndex++ }

    fun uniformLocation(name: String): Int =
        uniformLocations.getOrPut(name) { uniformLocations.size }

    fun uniform1i(name: String, value: Int) { uniformMap[name] = value }
    fun uniform1f(name: String, value: Float) { uniformMap[name] = value }
    fun uniform2f(name: String, x: Float, y: Float) { uniformMap[name] = floatArrayOf(x, y) }
    fun uniform3f(name: String, x: Float, y: Float, z: Float) { uniformMap[name] = floatArrayOf(x, y, z) }
    fun uniform4f(name: String, x: Float, y: Float, z: Float, w: Float) { uniformMap[name] = floatArrayOf(x, y, z, w) }
    fun uniformVector3(name: String, v: Vector3) { uniform3f(name, v.x, v.y, v.z) }
    fun uniformVector4(name: String, v: Vector4) { uniform4f(name, v.x, v.y, v.z, v.w) }
    fun uniformMatrix4(name: String, m: Matrix4) { uniformMap[name] = m }
    fun uniformSampler(name: String, unit: Int) { uniform1i(name, unit) }

    @Suppress("UNCHECKED_CAST")
    fun <T> getUniform(name: String): T? = uniformMap[name] as? T

    fun clearUniforms() = uniformMap.clear()

    fun destroy() {
        programId = 0
        vertShaderId = 0
        fragShaderId = 0
        isLinked = false
        isBound = false
        errors.clear()
        attribMap.clear()
        uniformMap.clear()
        uniformLocations.clear()
        nextAttribIndex = 0
    }

    override fun toString(): String = "LLGLSLShader($name, linked=$isLinked, bound=$isBound)"
}
