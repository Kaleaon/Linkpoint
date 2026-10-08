package com.linkpoint.linden.llrender

import com.linkpoint.linden.llmath.Matrix4
import com.linkpoint.linden.llcommon.LLError

object LLGL {

    private val errors: MutableList<String> = mutableListOf()

    fun checkError(tag: String = ""): Boolean {
        // No-op in JVM context; in real GL this would call glGetError()
        return errors.isEmpty()
    }

    fun getErrors(): List<String> = errors.toList()

    fun clearErrors() = errors.clear()

    fun logError(msg: String) {
        errors.add(msg)
        LLError.Log.error("LLGL", msg)
    }

    // ---- scoped enable/disable helpers ----

    class LLGLEnable(private val cap: GLCapability) : AutoCloseable {
        private val wasEnabled = LLGLState.isEnabled(cap)
        init { LLGLState.enable(cap) }
        override fun close() { if (!wasEnabled) LLGLState.disable(cap) }
    }

    class LLGLDisable(private val cap: GLCapability) : AutoCloseable {
        private val wasEnabled = LLGLState.isEnabled(cap)
        init { LLGLState.disable(cap) }
        override fun close() { if (wasEnabled) LLGLState.enable(cap) }
    }

    // ---- matrix stack ----

    private val matrixStack: ArrayDeque<Matrix4> = ArrayDeque()
    var currentMatrix: Matrix4 = Matrix4()
        private set

    fun pushMatrix() {
        if (matrixStack.size >= RenderConstants.MATRIX_STACK_DEPTH) {
            logError("Matrix stack overflow")
            return
        }
        matrixStack.addLast(currentMatrix)
    }

    fun popMatrix() {
        if (matrixStack.isEmpty()) {
            logError("Matrix stack underflow")
            return
        }
        currentMatrix = matrixStack.removeLast()
    }

    fun loadIdentity() {
        currentMatrix = Matrix4()
    }

    fun loadMatrix(m: Matrix4) {
        currentMatrix = m
    }

    fun multMatrix(m: Matrix4) {
        currentMatrix = currentMatrix * m
    }

    fun getMatrixStackDepth(): Int = matrixStack.size
}
