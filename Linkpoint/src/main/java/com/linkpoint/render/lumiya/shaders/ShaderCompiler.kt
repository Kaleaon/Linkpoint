package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32
import android.util.Log

/**
 * Compiles and links GLSL ES 3.20 shader programs.
 *
 * Design lineage: Lumiya `Shader.java` + `ShaderPreprocessor.java`, modernised
 * to GLSL ES 3.20 with support for:
 *   - Uniform Buffer Objects (std140 layout)
 *   - Shader Storage Buffers
 *   - Explicit attribute locations  (layout(location = N))
 *   - Explicit uniform binding points
 *   - Preprocessor macros (#define injection)
 */
class ShaderCompiler {

    companion object {
        private const val TAG = "ShaderCompiler"
    }

    /**
     * Compile a vertex + fragment program, returning the linked program handle.
     * Returns 0 on failure.
     */
    fun compile(
        vertexSource: String,
        fragmentSource: String,
        defines: Map<String, String> = emptyMap()
    ): Int {
        val vertHandle = compileStage(GLES32.GL_VERTEX_SHADER, preprocess(vertexSource, defines))
        if (vertHandle == 0) return 0

        val fragHandle = compileStage(GLES32.GL_FRAGMENT_SHADER, preprocess(fragmentSource, defines))
        if (fragHandle == 0) {
            GLES32.glDeleteShader(vertHandle)
            return 0
        }

        val program = GLES32.glCreateProgram()
        GLES32.glAttachShader(program, vertHandle)
        GLES32.glAttachShader(program, fragHandle)
        GLES32.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val info = GLES32.glGetProgramInfoLog(program)
            Log.e(TAG, "Program link failed:\n$info")
            GLES32.glDeleteProgram(program)
            GLES32.glDeleteShader(vertHandle)
            GLES32.glDeleteShader(fragHandle)
            return 0
        }

        // Shaders can be detached after linking
        GLES32.glDetachShader(program, vertHandle)
        GLES32.glDetachShader(program, fragHandle)
        GLES32.glDeleteShader(vertHandle)
        GLES32.glDeleteShader(fragHandle)

        return program
    }

    // ─────────────────────────────────────────────────────────────────────

    private fun compileStage(type: Int, source: String): Int {
        val shader = GLES32.glCreateShader(type)
        GLES32.glShaderSource(shader, source)
        GLES32.glCompileShader(shader)

        val status = IntArray(1)
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            val info = GLES32.glGetShaderInfoLog(shader)
            val typeName = if (type == GLES32.GL_VERTEX_SHADER) "VERTEX" else "FRAGMENT"
            Log.e(TAG, "$typeName shader compile failed:\n$info")
            GLES32.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    /**
     * Preprocess GLSL source: inject #define lines after #version and evaluate
     * #ifdef / #ifndef / #else / #endif conditionals against the defines map.
     * If no #version directive is present one is prepended.
     */
    private fun preprocess(source: String, defines: Map<String, String>): String {
        // Symbols known at preprocessing time (from caller-supplied defines)
        val definedSymbols = defines.keys.toMutableSet()

        // Stack-based conditional tracking.
        // Each frame records (parentIncluding, conditionTrue); effective include =
        // parentIncluding && conditionTrue for the innermost frame.
        data class CondFrame(val parentIncluding: Boolean, val conditionTrue: Boolean)
        val condStack = ArrayDeque<CondFrame>()

        fun isIncluding(): Boolean {
            val top = condStack.lastOrNull() ?: return true
            return top.parentIncluding && top.conditionTrue
        }

        val sb = StringBuilder()
        var versionEmitted = false

        for (line in source.lines()) {
            val trimmed = line.trimStart()
            when {
                trimmed.startsWith("#ifdef ") -> {
                    val sym = trimmed.removePrefix("#ifdef ").trim()
                    condStack.addLast(CondFrame(isIncluding(), sym in definedSymbols))
                }
                trimmed.startsWith("#ifndef ") -> {
                    val sym = trimmed.removePrefix("#ifndef ").trim()
                    condStack.addLast(CondFrame(isIncluding(), sym !in definedSymbols))
                }
                trimmed == "#else" -> {
                    val top = condStack.removeLastOrNull()
                    if (top != null) condStack.addLast(CondFrame(top.parentIncluding, !top.conditionTrue))
                }
                trimmed == "#endif" -> condStack.removeLastOrNull()
                !isIncluding() -> { /* skip excluded line */ }
                trimmed.startsWith("#define ") -> {
                    val sym = trimmed.removePrefix("#define ").trim().split("\\s+".toRegex()).firstOrNull().orEmpty()
                    if (sym.isNotEmpty()) definedSymbols.add(sym)
                    sb.appendLine(line)
                }
                trimmed.startsWith("#undef ") -> {
                    val sym = trimmed.removePrefix("#undef ").trim()
                    definedSymbols.remove(sym)
                    sb.appendLine(line)
                }
                else -> {
                    sb.appendLine(line)
                    if (!versionEmitted && trimmed.startsWith("#version")) {
                        versionEmitted = true
                        for ((k, v) in defines) sb.appendLine("#define $k $v")
                    }
                }
            }
        }

        if (!versionEmitted) {
            val header = buildString {
                appendLine("#version 320 es")
                for ((k, v) in defines) appendLine("#define $k $v")
            }
            return header + sb.toString()
        }
        return sb.toString()
    }
}
