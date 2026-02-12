package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32
import android.util.Log

/**
 * Abstract base for all Lumiya shader programs.
 *
 * Design lineage: Lumiya `ShaderProgram.java`, modernised.
 *
 * Subclasses provide GLSL source via [vertexSource] / [fragmentSource] and
 * resolve their uniform locations in [onBind].
 */
abstract class BaseShaderProgram {

    companion object {
        private const val TAG = "ShaderProgram"
    }

    /** GL program handle (0 = not compiled). */
    var handle: Int = 0; protected set

    /** Whether the program compiled and linked successfully. */
    val isCompiled: Boolean get() = handle != 0

    // ── Source ────────────────────────────────────────────────────────────

    protected abstract val vertexSource: String
    protected abstract val fragmentSource: String

    /** Optional preprocessor defines injected into both shaders. */
    protected open val defines: Map<String, String> = emptyMap()

    // ── Lifecycle ─────────────────────────────────────────────────────────

    fun compile(compiler: ShaderCompiler): Boolean {
        handle = compiler.compile(vertexSource, fragmentSource, defines)
        if (handle == 0) {
            Log.e(TAG, "${javaClass.simpleName} failed to compile")
            return false
        }
        onBind()
        return true
    }

    /** Called after successful link – resolve uniform / UBO locations here. */
    protected abstract fun onBind()

    fun use() {
        GLES32.glUseProgram(handle)
    }

    fun destroy() {
        if (handle != 0) {
            GLES32.glDeleteProgram(handle)
            handle = 0
        }
    }

    // ── Uniform helpers ──────────────────────────────────────────────────

    protected fun loc(name: String): Int {
        val l = GLES32.glGetUniformLocation(handle, name)
        if (l < 0) Log.w(TAG, "${javaClass.simpleName}: uniform '$name' not found")
        return l
    }

    protected fun attrLoc(name: String): Int {
        val l = GLES32.glGetAttribLocation(handle, name)
        if (l < 0) Log.w(TAG, "${javaClass.simpleName}: attribute '$name' not found")
        return l
    }

    protected fun uboIndex(name: String): Int {
        return GLES32.glGetUniformBlockIndex(handle, name)
    }
}
